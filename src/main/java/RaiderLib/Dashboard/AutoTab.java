package RaiderLib.Dashboard;

import com.choreo.lib.Choreo;
import com.choreo.lib.ChoreoTrajectory;

import RaiderLib.Subsystems.Drivetrains.SwerveDrive;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.trajectory.Trajectory;
import edu.wpi.first.math.trajectory.TrajectoryConfig;
import edu.wpi.first.math.trajectory.TrajectoryGenerator;
import edu.wpi.first.networktables.GenericEntry;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEvent.Kind;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;
import edu.wpi.first.wpilibj.smartdashboard.FieldObject2d;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class AutoTab extends SubsystemBase {
  private List<ChoreoTrajectory> m_trajectories = new ArrayList<>();
  private String m_feedbackValue = "Enter a command!";
  private Command m_autoCommand = Commands.runOnce(() -> {});
  private Alliance m_alliance;
  private Field2d m_field;
  private HashMap<Character, Command> m_actionMap;
  private SwerveDrive m_drivetrain;
  private List<Command> m_startCommands;
  private List<Command> m_endCommands;
  private HashMap<Character, Pose2d> m_startPositions;

  private GenericEntry autoStringEntry;
  private GenericEntry safetyEntry;

  public AutoTab(
      HashMap<Character, Command> actionMap,
      SwerveDrive drivetrain,
      List<Command> startCommands,
      List<Command> endCommands,
      HashMap<Character, Pose2d> startPositions) {
    NetworkTableInstance nt = NetworkTableInstance.getDefault();
    NetworkTable table = nt.getTable("Shuffleboard").getSubTable("Customize Auto");

    autoStringEntry = table.getTopic("Enter Command").getGenericEntry();
    safetyEntry = table.getTopic("Ignore Safety").getGenericEntry();

    m_alliance = DriverStation.getAlliance().get();
    m_field = new Field2d();

    m_actionMap = actionMap;
    m_drivetrain = drivetrain;
    m_startCommands = startCommands;
    m_endCommands = endCommands;
    m_startPositions = startPositions;

    setupAutoTab();
  }

  public void clearField() {
    for (int i = 0; i < 100; i++) {
      FieldObject2d obj = m_field.getObject("traj" + i);
      obj.setTrajectory(new Trajectory());
    }
  }

  public void drawPaths() {
    clearField();
    for (int i = 0; i < m_trajectories.size(); i++) {
      ChoreoTrajectory pathTraj = m_trajectories.get(i);
      List<Pose2d> poses = Arrays.asList(pathTraj.getPoses());
      Trajectory displayTraj =
          TrajectoryGenerator.generateTrajectory(poses, new TrajectoryConfig(AutoTabConfig.kMaxSpeed, AutoTabConfig.kMaxAcceleration));
      m_field.getObject("traj" + i).setTrajectory(displayTraj);
    }
  }

  public void clearAll() {
    m_trajectories.clear();
    clearField();
  }

  public void setFeedback(String feedback) {
    m_feedbackValue = feedback;
  }

  public String getFeedback() {
    return m_feedbackValue;
  }

  public void setupAutoTab() {
    ShuffleboardTab autoTab = Shuffleboard.getTab("Auto");

    autoTab.add("Enter Command", "").withSize(3, 1).withPosition(0, 0);
    autoTab.add(m_field).withSize(6, 4).withPosition(3, 0);
    autoTab.addString("Feedback", () -> m_feedbackValue).withSize(3, 1).withPosition(0, 1);

    autoTab
        .add("Ignore Safety", false)
        .withWidget(BuiltInWidgets.kToggleSwitch)
        .withSize(2, 1)
        .withPosition(0, 2);

    NetworkTableInstance inst = NetworkTableInstance.getDefault();
    NetworkTable ntTable = inst.getTable("Shuffleboard").getSubTable("Auto");

    ntTable.addListener(
        "Enter Command",
        EnumSet.of(Kind.kValueAll),
        (table, key, event) -> {
          generatePaths();
        });

    ntTable.addListener(
        "Ignore Safety",
        EnumSet.of(Kind.kValueAll),
        (table, key, event) -> {
          generatePaths();
        });
  }

  public void generatePaths() {
    String autoString = autoStringEntry.getString("");
    String[] words = autoString.split(" ");
    boolean ignoreSafety = safetyEntry.getBoolean(false);

    if (m_startPositions.get(autoString.charAt(0)) == null) {
      setFeedback("Invalid start position");
      return;
    }

    SequentialCommandGroup finalPath = new SequentialCommandGroup();
    m_trajectories.clear();

    for (Command startCommand : m_startCommands) {
      finalPath.addCommands(startCommand);
    }

    if (autoString.length() == 0) {
      for (Command endCommand : m_endCommands) {
        finalPath.addCommands(endCommand);
      }
      m_autoCommand = finalPath;
      setFeedback("Empty path. Is this intentional?");
      return;
    }

    char lastPose = ' ';
    for (int i = 0; i < words.length; i++) {
      ParallelCommandGroup group = new ParallelCommandGroup();
      for (int j = 0; j < words[i].length(); j++) {
        char current = words[i].charAt(j);
        if (Character.isLetter(current)) {
          if (i == 0) {
            lastPose = current;
            continue;
          }
          if (lastPose != ' ') {
            try {
              ChoreoTrajectory path = Choreo.getTrajectory("" + lastPose + "-" + current);
              m_trajectories.add(path);
              group.addCommands(Choreo.choreoSwerveCommand(
                path,
                m_drivetrain::getPose,
                new PIDController(AutoTabConfig.kPXController, 0, 0),
                new PIDController(AutoTabConfig.kPXController, 0, 0),
                new PIDController(AutoTabConfig.kPThetaController, 0, 0),
                (ChassisSpeeds speeds) -> {
                  m_drivetrain.drive(new Translation2d(speeds.vxMetersPerSecond, speeds.vyMetersPerSecond), speeds.omegaRadiansPerSecond, false);
                },
                () -> {
                    Optional<DriverStation.Alliance> alliance = DriverStation.getAlliance();
                    return alliance.isPresent() && alliance.get() == Alliance.Red;
                },
                m_drivetrain
              ));
            } catch (Exception e) {
              setFeedback("Path File Not Found");
              m_autoCommand = Commands.runOnce(() -> {});
            }
          }
        } else if (Character.isDigit(current) && m_actionMap.containsKey(current)) {
          group.addCommands(m_actionMap.get(current));
        }
      }
      finalPath.addCommands(group);
    }
    setFeedback("Successfully Created Auto Sequence!");
    m_autoCommand = finalPath;
  }

  public Command getAutoCommand() {
    return m_autoCommand;
  }

  public Pose2d getStartPose() {
    return m_startPositions.get(autoStringEntry.getString("").charAt(0));
  }

  @Override
  public void periodic() {
    if (DriverStation.isDisabled()) {
      if (DriverStation.getAlliance().isPresent()
          && DriverStation.getAlliance().get() != m_alliance) {
        m_alliance = DriverStation.getAlliance().get();
      }
    }
  }
}
