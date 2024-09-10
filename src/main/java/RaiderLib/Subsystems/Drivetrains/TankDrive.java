// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package RaiderLib.Subsystems.Drivetrains;

import org.photonvision.EstimatedRobotPose;

import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;

import RaiderLib.Config.MotorConfiguration;
import RaiderLib.Drivers.IMUs.IMU;
import RaiderLib.Drivers.Motors.Motor;
import RaiderLib.Drivers.Motors.MotorFactory;
import RaiderLib.Drivers.Motors.Motor.MotorType;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.estimator.DifferentialDrivePoseEstimator;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.DifferentialDriveKinematics;
import edu.wpi.first.math.kinematics.DifferentialDriveOdometry;
import edu.wpi.first.math.kinematics.DifferentialDriveWheelPositions;
import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.*;

/* This class declares the subsystem for the robot drivetrain if controllers are connected via CAN. Make sure to go to
 * RobotContainer and uncomment the line declaring this subsystem and comment the line for PWMDrivetrain.
 *
 * The subsystem contains the objects for the hardware contained in the mechanism and handles low level logic
 * for control. Subsystems are a mechanism that, when used in conjuction with command "Requirements", ensure
 * that hardware is only being used by 1 command at a time.
 */
public class TankDrive extends SubsystemBase {
  /*Class member variables. These variables represent things the class needs to keep track of and use between
  different method calls. */
  private Motor m_leftMaster;
  private Motor m_leftSlave;
  private Motor m_rightMaster;
  private Motor m_rightSlave;

  private DifferentialDrivePoseEstimator m_odometry;
  private IMU m_gyro;

  public TankDrive(
      MotorType MotorType,
      MotorConfiguration MotorConfig,
      IMU imu,
      DifferentialDriveKinematics kinematics) {

    m_leftMaster = MotorFactory.createMotor(MotorType, MotorConfig);
    m_leftSlave = MotorFactory.createMotor(MotorType, MotorConfig);
    m_rightMaster = MotorFactory.createMotor(MotorType, MotorConfig);
    m_rightSlave = MotorFactory.createMotor(MotorType, MotorConfig);

    m_odometry = new DifferentialDrivePoseEstimator(kinematics,
        new Rotation2d(0),
        0,
        0,
        new Pose2d(new Translation2d(0, 0), new Rotation2d(0)));

    m_gyro = imu;

    m_leftMaster.setFollower(m_leftSlave);
    m_rightMaster.setFollower(m_rightSlave);

    // Invert the left side so both sides drive forward with positive motor outputs
    // m_leftMaster.setInverted(false);
    // m_leftSlave.setInverted(false);
    // m_rightMaster.setInverted(true);
    // m_rightSlave.setInverted(true);
    // TODO implement inversion for motors

    // m_leftMaster.setIdleMode(IdleMode.kCoast);
    // m_leftSlave.setIdleMode(IdleMode.kCoast);
    // m_rightMaster.setIdleMode(IdleMode.kCoast);
    // m_rightSlave.setIdleMode(IdleMode.kCoast);
    // TODO implement idle modes

    m_leftMasterController.setP(DriveConstants.kLeftPIDF[0], 0);
    m_leftMasterController.setI(DriveConstants.kLeftPIDF[1], 0);
    m_leftMasterController.setD(DriveConstants.kLeftPIDF[2], 0);
    m_leftMasterController.setFF(DriveConstants.kLeftPIDF[3], 0);

    m_leftSlaveController.setP(DriveConstants.kLeftPIDF[0], 0);
    m_leftSlaveController.setI(DriveConstants.kLeftPIDF[1], 0);
    m_leftSlaveController.setD(DriveConstants.kLeftPIDF[2], 0);
    m_leftSlaveController.setFF(DriveConstants.kLeftPIDF[3], 0);

    m_rightMasterController.setP(DriveConstants.kRightPIDF[0], 0);
    m_rightMasterController.setI(DriveConstants.kRightPIDF[1], 0);
    m_rightMasterController.setD(DriveConstants.kRightPIDF[2], 0);
    m_rightMasterController.setFF(DriveConstants.kRightPIDF[3], 0);

    m_rightSlaveController.setP(DriveConstants.kRightPIDF[0], 0);
    m_rightSlaveController.setI(DriveConstants.kRightPIDF[1], 0);
    m_rightSlaveController.setD(DriveConstants.kRightPIDF[2], 0);
    m_rightSlaveController.setFF(DriveConstants.kRightPIDF[3], 0);

    resetOdometry();
    resetGyro();
    resetEncoders();
  }

  /*Method to control the drivetrain using arcade drive. Arcade drive takes a speed in the X (forward/back) direction
   * and a rotation about the Z (turning the robot about it's center) and uses these to control the drivetrain motors */
  public void drive(double speed, double rotation, boolean squareInputs, boolean isAuto) {
    speed = MathUtil.applyDeadband(speed, OperatorConstants.kDriveDeadband);
    rotation = MathUtil.applyDeadband(rotation, OperatorConstants.kTurnDeadband);
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
      leftSpeed *= AutoConstants.kMaxAutoRPM;
      rightSpeed *= AutoConstants.kMaxAutoRPM;
    } else {
      leftSpeed *= maxSpeed;
      rightSpeed *= maxSpeed;
    }

    runLeft(leftSpeed);
    runRight(rightSpeed);
  }

  public void runLeft(double rpm) {
    m_leftMasterController.setReference(rpm, ControlType.kVelocity);
    m_leftSlaveController.setReference(rpm, ControlType.kVelocity);
  }

  public void runRight(double rpm) {
    m_rightMasterController.setReference(rpm, ControlType.kVelocity);
    m_rightSlaveController.setReference(rpm, ControlType.kVelocity);
  }

  public void stop() {
    this.drive(0, 0, false, false);
  }

  public double getRightPosition() {
    return m_rightEncoder.getPosition();
  }

  public double getLeftPosition() {
    return m_leftEncoder.getPosition();
  }

  public double getRightVelocity() {
    return m_rightEncoder.getVelocity();
  }

  public double getLeftVelocity() {
    return m_leftEncoder.getVelocity();
  }

  public double getRightVelocityMeters() {
    return getRightVelocity() * DriveConstants.kVelocityConversionFactor;
  }

  public double getLeftVelocityMeters() {
    return getLeftVelocity() * DriveConstants.kVelocityConversionFactor;
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

  public void resetEncoders() {
    m_leftEncoder.setPosition(0);
    m_rightEncoder.setPosition(0);
  }

  public void toggleGear() {
    isHighGear = !isHighGear;
    if (isHighGear) {
      maxSpeed = DriveConstants.kMaxHighRPM;
    } else {
      maxSpeed = DriveConstants.kMaxLowRPM;
    }
  }

  public boolean getGear() {
    return isHighGear;
  }

  @Override
  public void periodic() {
    /*This method will be called once per scheduler run. It can be used for running tasks we know we want to update each
     * loop, such as processing sensor data. Our drivetrain is simple so we don't have anything to put here. */

    m_odometry.update(
        Rotation2d.fromDegrees(getHeading()),
        new DifferentialDriveWheelPositions(getLeftPosition(), getRightPosition()));

    EstimatedRobotPose pose = m_camera.getEstimatedPose();
    m_odometry.addVisionMeasurement(pose.estimatedPose.toPose2d(), pose.timestampSeconds);

    // m_field.setRobotPose(m_odometry.getPoseMeters());

    // SmartDashboard.putNumber("leftEncoderPosition", getLeftPosition());
    // SmartDashboard.putNumber("rightEncoderPosition", getRightPosition());
    SmartDashboard.putNumber("heading", getHeading());
    SmartDashboard.putNumber("leftVelocity", getLeftVelocityMeters());
    SmartDashboard.putNumber("rightVelocity", getRightVelocityMeters());
    SmartDashboard.putString("Gear", isHighGear ? "High" : "Low");
  }
}
