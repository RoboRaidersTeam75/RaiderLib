package RaiderLib.Dashboard;

import com.choreo.lib.ChoreoTrajectory;
import edu.wpi.first.math.geometry.Pose2d;
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
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;

public class AutoTab {
  private List<ChoreoTrajectory> trajectories = new ArrayList<>();
  private String feedbackValue = "Enter a command!";
  private Command autoCommand = Commands.runOnce(() -> {});
  private Alliance alliance;
  private Field2d field;
  private HashMap<Character, Command> commandMap;

  private GenericEntry autoStringEntry;
  private GenericEntry safetyEntry;

  public AutoTab(HashMap<Character, Command> commands) {
    NetworkTableInstance nt = NetworkTableInstance.getDefault();
    NetworkTable table = nt.getTable("Shuffleboard").getSubTable("Customize Auto");

    autoStringEntry = table.getTopic("Enter Command").getGenericEntry();
    safetyEntry = table.getTopic("Ignore Safety").getGenericEntry();

    alliance = DriverStation.getAlliance().get();
    field = new Field2d();
    commandMap = commands;

    setupAutoTab();
  }

  public void clearField() {
    for (int i = 0; i < 100; i++) {
      FieldObject2d obj = field.getObject("traj" + i);
      obj.setTrajectory(new Trajectory());
    }
  }

  public void drawPaths() {
    clearField();
    for (int i = 0; i < trajectories.size(); i++) {
      ChoreoTrajectory pathTraj = trajectories.get(i);
      List<Pose2d> poses = Arrays.asList(pathTraj.getPoses());
      Trajectory displayTraj =
          TrajectoryGenerator.generateTrajectory(poses, new TrajectoryConfig(5, 3));
      field.getObject("traj" + i).setTrajectory(displayTraj);
    }
  }

  public void clearAll() {
    trajectories.clear();
    clearField();
  }

  public void setFeedback(String feedback) {
    feedbackValue = feedback;
  }

  public String getFeedback() {
    return feedbackValue;
  }

  public void setupAutoTab() {
    ShuffleboardTab autoTab = Shuffleboard.getTab("Auto");

    autoTab.add("Enter Command", "").withSize(3, 1).withPosition(0, 0);
    autoTab.add(field).withSize(6, 4).withPosition(3, 0);
    autoTab.addString("Feedback", () -> feedbackValue).withSize(3, 1).withPosition(0, 1);

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
    boolean ignoreSafety = safetyEntry.getBoolean(false);

    SequentialCommandGroup finalPath = new SequentialCommandGroup();
    trajectories.clear();

    if (autoString.length() == 0) {
      return;
    }


  }
}
