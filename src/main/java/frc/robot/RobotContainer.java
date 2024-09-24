package frc.robot;

import RaiderLib.Config.MotorConfiguration;
import RaiderLib.Config.PIDConstants;
import RaiderLib.Drivers.IMUs.IMU;
import RaiderLib.Drivers.IMUs.NavX;
import RaiderLib.Drivers.IMUs.Pigeon2;
import RaiderLib.Drivers.Motors.Motor.MotorType;
import RaiderLib.Subsystems.Drivetrains.SwerveDrive;
import RaiderLib.Subsystems.Drivetrains.TankDrive;
import RaiderLib.Config.SwerveConstants;
import RaiderLib.Logging.Logger;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.DataLogManager;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import frc.robot.commands.TeleopSwerve;


/**
 * This class is where the bulk of the robot should be declared. Since Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in the {@link Robot}
 * periodic methods (other than the scheduler calls). Instead, the structure of the robot (including
 * subsystems, commands, and button mappings) should be declared here.
 */
public class RobotContainer {
  /* Controllers */
  private final Joystick LeftStick = new Joystick(0);
  private final Joystick RightStick = new Joystick(1);

  private final CommandXboxController m_XboxController = new CommandXboxController(2);

  private final IMU m_Imu = new Pigeon2(0);

  double wheelBase = Units.inchesToMeters(22.5);
  double trackWidth = Units.inchesToMeters(22.5);
  double wheelCircumference = 0;

  private final SwerveConstants swerveConstants = new SwerveConstants()
  .setKinematics(new SwerveDriveKinematics(
    new Translation2d(wheelBase / 2.0, trackWidth / 2.0),
    new Translation2d(wheelBase / 2.0, -trackWidth / 2.0),
    new Translation2d(-wheelBase / 2.0, trackWidth / 2.0),
    new Translation2d(-wheelBase / 2.0, -trackWidth / 2.0)
  ))
  .setWheelBase(wheelBase)
  .setTrackWidth(trackWidth)
  .setWheelCircumference(wheelCircumference)
  .setDriveGearRatio(6.75 / 1.0)
  .setAngleGearRatio(12.8 / 1.0)
  .setMaxSpeed(6);

  private MotorConfiguration angleTemplate = new MotorConfiguration()
  .setSupplyCurrentLimit(40)
  .setSupplyCurrentThresholdAmps(40)
  .setSupplyCurrentThresholdSeconds(.1)
  .setOpenLoopRampRateSeconds(.25)
  .setClosedLoopRampRateSeconds(0)
  .setCanbus("75Drive") // Neutral modes??? Angle offsets??
  .setPID(new PIDConstants(4.8, 0, 0));

  private MotorConfiguration driveTemplate = new MotorConfiguration()
  .setSupplyCurrentLimit(40)
  .setSupplyCurrentThresholdAmps(60)
  .setSupplyCurrentThresholdSeconds(.1)
  .setOpenLoopRampRateSeconds(.25)
  .setClosedLoopRampRateSeconds(0)
  .setCanbus("75Drive")
  .setPID(new PIDConstants(.05, 0, 0));

  

  private final SwerveDrive m_Swerve = new SwerveDrive(swerveConstants, MotorType.KRAKENX60, driveTemplate, angleTemplate, 
        new int[]{0,0,0,0},
        new int[]{0,0,0,0},
        m_Imu);
  /* Driver Buttons */

  /*Auto Chooser Config */
  // private final SendableChooser<Command> m_AutoChooser;

  /** The container for the robot. Contains subsystems, OI devices, and commands. */
  public RobotContainer() {

    DriverStation.startDataLog(DataLogManager.getLog());

    configureButtonBindings();
  }

  /**
   * Use this method to define your button->command mappings. Buttons can be created by
   * instantiating a {@link GenericHID} or one of its subclasses ({@link
   * edu.wpi.first.wpilibj.Joystick} or {@link XboxController}), and then passing it to a {@link
   * edu.wpi.first.wpilibj2.command.button.JoystickButton}.
   */
  private void configureButtonBindings() {
    m_Swerve.setDefaultCommand(
      new TeleopSwerve(
          m_Swerve,
          () -> LeftStick.getY(),
          () -> LeftStick.getX(),
          () -> RightStick.getX() ,
          true
         ));

  }

  /**
   * Use this to pass the autonomous command to the main {@link Robot} class.
   *
   * @return the command to run in autonomous
   */
  public Command getAutonomousCommand() {
    // An ExampleCommand will run in autonomous

    // return m_AutoChooser.getSelected();
    return null;
  }
}
