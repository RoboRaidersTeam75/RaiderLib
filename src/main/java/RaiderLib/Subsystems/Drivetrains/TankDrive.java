// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package RaiderLib.Subsystems.Drivetrains;

import RaiderLib.Config.MotorConfiguration;
import RaiderLib.Config.PIDConstants;
import RaiderLib.Drivers.IMUs.IMU;
import RaiderLib.Drivers.Motors.Motor;
import RaiderLib.Drivers.Motors.Motor.MotorType;
import RaiderLib.Drivers.Motors.MotorFactory;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.estimator.DifferentialDrivePoseEstimator;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.DifferentialDriveKinematics;
import edu.wpi.first.math.kinematics.DifferentialDriveWheelPositions;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

/* This class declares the subsystem for the robot drivetrain if controllers are connected via CAN. Make sure to go to
 * RobotContainer and uncomment the line declaring this subsystem and comment the line for PWMDrivetrain.
 *
 * The subsystem contains the objects for the hardware contained in the mechanism and handles low level logic
 * for control. Subsystems are a mechanism that, when used in conjuction with command "Requirements", ensure
 * that hardware is only being used by 1 command at a time.
 */
public class TankDrive extends SubsystemBase {
  /*
   * Class member variables. These variables represent things the class needs to
   * keep track of and use between
   * different method calls.
   */
  private Motor m_leftMaster;
  private Motor m_leftSlave;
  private Motor m_rightMaster;
  private Motor m_rightSlave;

  private DifferentialDrivePoseEstimator m_odometry;
  private IMU m_gyro;

  private PIDConstants m_leftPID;
  private PIDConstants m_rightPID;

  private double m_autoMaxSpeed;
  private double m_teleopMaxSpeed;
  private double velocityConversionFactor;

  public TankDrive(
      MotorType MotorType,
      MotorConfiguration rightMotorConfig,
      MotorConfiguration leftMotorConfig,
      IMU imu,
      DifferentialDriveKinematics kinematics,
      double autoMaxSpeed,
      double teleOpMaxSpeed,
      double wheelDiameter) {

    m_leftMaster = MotorFactory.createMotor(MotorType, leftMotorConfig);
    m_leftSlave = MotorFactory.createMotor(MotorType, leftMotorConfig);
    m_rightMaster = MotorFactory.createMotor(MotorType, rightMotorConfig);
    m_rightSlave = MotorFactory.createMotor(MotorType, rightMotorConfig);

    m_odometry =
        new DifferentialDrivePoseEstimator(
            kinematics,
            new Rotation2d(0),
            0,
            0,
            new Pose2d(new Translation2d(0, 0), new Rotation2d(0)));

    m_gyro = imu;

    m_leftMaster.setFollower(m_leftSlave);
    m_rightMaster.setFollower(m_rightSlave);

    m_autoMaxSpeed = autoMaxSpeed;
    m_teleopMaxSpeed = teleOpMaxSpeed;
    velocityConversionFactor = Math.PI * wheelDiameter / 60;

    resetOdometry();
    resetGyro();
    resetEncoders();
  }

  /*
   * Method to control the drivetrain using arcade drive. Arcade drive takes a
   * speed in the X (forward/back) direction
   * and a rotation about the Z (turning the robot about it's center) and uses
   * these to control the drivetrain motors
   */
  public void drive(double speed, double rotation, boolean squareInputs, boolean isAuto) {
    speed = MathUtil.applyDeadband(speed, 0.15); // HARD-CODED
    rotation = MathUtil.applyDeadband(rotation, 0.15); // HARD-CODED
    speed = MathUtil.clamp(speed, -1.0, 1.0);
    rotation = MathUtil.clamp(rotation, -1.0, 1.0);

    rotation *= 0.8;

    // decrease sensitivity at lower inputs
    if (squareInputs) {
      speed = Math.copySign(speed * speed, speed);
      rotation = Math.copySign(rotation * rotation, rotation);
    }
    double leftSpeed = speed - rotation;
    double rightSpeed = speed + rotation;

    if (isAuto) {
      leftSpeed *= m_autoMaxSpeed;
      rightSpeed *= m_autoMaxSpeed;
    } else {
      leftSpeed *= m_teleopMaxSpeed;
      rightSpeed *= m_teleopMaxSpeed;
    }

    runLeft(leftSpeed);
    runRight(rightSpeed);
  }

  public void runLeft(double rpm) {
    m_leftMaster.setRPM(rpm);
  }

  public void runRight(double rpm) {
    m_rightMaster.setRPM(rpm);
  }

  public void stop() {
    this.drive(0, 0, false, false);
  }

  public double getRightPosition() {
    return m_rightMaster.getPosition();
  }

  public double getLeftPosition() {
    return m_leftMaster.getPosition();
  }

  public double getRightVelocity() {
    return m_rightMaster.getRPM();
  }

  public double getLeftVelocity() {
    return m_leftMaster.getRPM();
  }

  public double getRightVelocityMeters() {
    return getRightVelocity() * velocityConversionFactor;
  }

  public double getLeftVelocityMeters() {
    return getLeftVelocity() * velocityConversionFactor;
  }

  public double getHeading() {
    return m_gyro.getAngle();
  }

  public Pose2d getPose() {
    return m_odometry.getEstimatedPosition();
  }

  public void resetOdometry() {
    m_odometry.resetPosition(
        Rotation2d.fromDegrees(getHeading()),
        new DifferentialDriveWheelPositions(getLeftPosition(), getRightPosition()),
        getPose());
  }

  public void resetGyro() {
    m_gyro.reset();
  }

  public void resetEncoders() { // TODO implement
    // m_leftEncoder.setPosition(0);
    // m_rightEncoder.setPosition(0);
  }

  @Override
  public void periodic() {
    /*
     * This method will be called once per scheduler run. It can be used for running
     * tasks we know we want to update each
     * loop, such as processing sensor data. Our drivetrain is simple so we don't
     * have anything to put here.
     */

    m_odometry.update(
        Rotation2d.fromDegrees(getHeading()),
        new DifferentialDriveWheelPositions(getLeftPosition(), getRightPosition()));

    SmartDashboard.putNumber("heading", getHeading());
    SmartDashboard.putNumber("leftVelocity", getLeftVelocityMeters());
    SmartDashboard.putNumber("rightVelocity", getRightVelocityMeters());
  }
}
