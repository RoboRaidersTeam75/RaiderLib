package RaiderLib.Subsystems.Drivetrains;

import RaiderLib.Config.MotorConfiguration;
import RaiderLib.Config.SwerveConstants;
import RaiderLib.Drivers.IMUs.IMU;
import RaiderLib.Drivers.Motors.Motor.MotorType;
import edu.wpi.first.math.estimator.SwerveDrivePoseEstimator;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class SwerveDrive extends SubsystemBase {
  private SwerveDrivePoseEstimator swerveOdometry;
  private SwerveModule[] m_modules;
  private SwerveConstants m_constants;

  private IMU m_imu;

  public SwerveDrive(
      SwerveConstants constants,
      MotorType type,
      MotorConfiguration driveConfig,
      MotorConfiguration angleConfig,
      IMU imu) {

    SwerveModule[] modules = new SwerveModule[4];
    for (int i = 0; i < 4; i++) {
      driveConfig.setCANID(constants.driveMotorCanIds[i]);
      angleConfig.setCANID(constants.angleMotorCanIds[i]);
      driveConfig.setMotorInvert(constants.inverts[i]);
      modules[i] = new SwerveModule(i, constants, type, driveConfig, angleConfig);
      Timer.delay(.2);
    }

    m_imu = imu;
    m_modules = modules;
    m_constants = constants;
    zeroGyro();

    swerveOdometry =
        new SwerveDrivePoseEstimator(
            constants.kinematics,
            m_imu.getRotation2d(),
            getModulePositions(),
            new Pose2d(0, 0, new Rotation2d(0)));
  }

  public void choreoDrive(double xSpeed, double ySpeed, double rot, boolean fieldRelative) {
    // System.out.println("Speed " + xSpeed + " " + ySpeed + " " + rot);
    SwerveModuleState[] swerveModuleStates =
        this.m_constants.kinematics.toSwerveModuleStates(
            fieldRelative
                ? ChassisSpeeds.fromFieldRelativeSpeeds(xSpeed, ySpeed, rot, m_imu.getRotation2d())
                : new ChassisSpeeds(xSpeed, ySpeed, rot));
    SwerveDriveKinematics.desaturateWheelSpeeds(swerveModuleStates, this.m_constants.maxSpeed);
    m_modules[0].setDesiredState(swerveModuleStates[0], false);
    m_modules[1].setDesiredState(swerveModuleStates[1], false);
    m_modules[2].setDesiredState(swerveModuleStates[2], false);
    m_modules[3].setDesiredState(swerveModuleStates[3], false);
  }

  /**
   * @param translation - X (Meters per second, Forwards/Backwards) and Y (Meters Per Second,
   *     Left/Right)
   * @param rotation - Yaw/angle of the robot (Counter Clockwise is positive)
   * @param openLoop - Use feedback and PID (if false)
   */
  public void drive(Translation2d translation, double rotation, boolean isOpenLoop) {
    SwerveModuleState[] swerveModuleStates =
        this.m_constants.kinematics.toSwerveModuleStates(
            ChassisSpeeds.fromFieldRelativeSpeeds(
                -translation.getX(), -translation.getY(), rotation, getRotation2D()));

    SwerveDriveKinematics.desaturateWheelSpeeds(swerveModuleStates, this.m_constants.maxSpeed);

    for (SwerveModule mod : m_modules) {
      mod.setDesiredState(swerveModuleStates[mod.m_moduleNumber], isOpenLoop);
    }
  }

  /**
   * Set the module states (used in autos)
   *
   * @param desiredStates The desired module state to set the wheels
   */
  public void setModuleStates(SwerveModuleState[] desiredStates) {
    SwerveDriveKinematics.desaturateWheelSpeeds(desiredStates, this.m_constants.maxSpeed);

    for (SwerveModule mod : m_modules) {
      mod.setDesiredState(desiredStates[mod.m_moduleNumber], true);
    }
  }

  public void stopModules() {
    for (SwerveModule mod : m_modules) {
      mod.setDesiredState(new SwerveModuleState(0, mod.getState().angle), true);
    }
  }

  /**
   * @return the estimated position of the robot
   */
  public Pose2d getPose() {
    Pose2d oldPose = swerveOdometry.getEstimatedPosition();
    Pose2d betterPose = new Pose2d(-oldPose.getX(), oldPose.getY(), oldPose.getRotation());
    return betterPose;
  }

  public Pose2d getChoreoPose() {
    Pose2d oldPose = swerveOdometry.getEstimatedPosition();
    Pose2d betterPose =
        new Pose2d(oldPose.getX() * (1.0 / 2.0), oldPose.getY(), oldPose.getRotation());
    return betterPose;
  }

  /**
   * resets the robot pose
   *
   * @param pose the desired pose to reset the robot to
   */
  public void setPose(Pose2d pose) {
    Pose2d betterPose = new Pose2d(-pose.getX(), pose.getY(), pose.getRotation());
    swerveOdometry.resetPosition(getRotation2D(), getModulePositions(), betterPose);
  }

  /**
   * returns the velocity and angle of all swerve modules
   *
   * @return the state of all swerve modules
   */
  public SwerveModuleState[] getModuleStates() {
    SwerveModuleState[] states = new SwerveModuleState[4];
    for (SwerveModule mod : m_modules) {
      states[mod.m_moduleNumber] = mod.getState();
    }

    return states;
  }

  /**
   * used for swerve drive logging
   *
   * @return the setpoint a module has been set to
   */
  private SwerveModuleState[] getModuleSetpoints() {
    SwerveModuleState[] states = new SwerveModuleState[4];
    for (SwerveModule mod : m_modules) {
      states[mod.m_moduleNumber] = mod.getState();
    }
    return states;
  }

  /**
   * returns the meters travelled and angle of the swerve module
   *
   * @return position of all swerve modules
   */
  public SwerveModulePosition[] getModulePositions() {
    SwerveModulePosition[] positions = new SwerveModulePosition[4];
    for (SwerveModule mod : m_modules) {
      positions[mod.m_moduleNumber] = mod.getPosition();
    }
    return positions;
  }

  /** resets the NavX (sets angle to 0) */
  public void zeroGyro() {
    m_imu.reset();
  }

  /**
   * gives a breaking heading (360->0 degrees for example) Takes into account a gyro invert
   *
   * @return rotation2d returned by the gyro
   */
  public Rotation2d getRotation2D() {
    // if (Constants.Swerve.invertGyro) {
    //   return Rotation2d.fromDegrees(-m_NavX.getYaw() + 180);
    // } else {

    // }
    return Rotation2d.fromDegrees(m_imu.getYaw() + 180);
  }

  @Override
  public void periodic() {
    swerveOdometry.update(m_imu.getRotation2d(), getModulePositions());
    for (SwerveModule mod : m_modules) {
      SmartDashboard.putNumber(
          "Mod " + mod.m_moduleNumber + " CANcoder", mod.getCanCoder().getDegrees());
    }
  }
}
