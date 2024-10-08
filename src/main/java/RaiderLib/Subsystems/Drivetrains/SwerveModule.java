package RaiderLib.Subsystems.Drivetrains;

import RaiderLib.Config.MotorConfiguration;
import RaiderLib.Config.SwerveConstants;
import RaiderLib.Drivers.Motors.Motor;
import RaiderLib.Drivers.Motors.Motor.MotorType;
import RaiderLib.Drivers.Motors.MotorFactory;
import RaiderLib.Util.Conversions;
import RaiderLib.Util.ModuleState;
import com.ctre.phoenix6.hardware.CANcoder;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;

public class SwerveModule {
  public int m_moduleNumber;

  private Motor m_driveMotor;
  public Motor m_angleMotor;
  private CANcoder m_angleEncoder;

  private Rotation2d lastAngle;

  private SwerveModuleState setpoint;

  private SwerveConstants m_constants;

  public SwerveModule(
      int moduleNumber,
      SwerveConstants constants,
      MotorType type,
      MotorConfiguration driveConfig,
      MotorConfiguration angleConfig) {
    this(moduleNumber, constants, type, driveConfig, angleConfig, "");
  }

  public SwerveModule(
      int moduleNumber,
      SwerveConstants constants,
      MotorType type,
      MotorConfiguration driveConfig,
      MotorConfiguration angleConfig,
      String canBus) {
    m_moduleNumber = moduleNumber;
    m_driveMotor = MotorFactory.createMotor(type, driveConfig);
    m_angleMotor = MotorFactory.createMotor(type, angleConfig);
    if (canBus.length() == 0) {
      m_angleEncoder = new CANcoder(constants.cancoderCanIds[moduleNumber]);
    } else {
      m_angleEncoder = new CANcoder(constants.cancoderCanIds[moduleNumber], canBus);
    }
    setpoint = new SwerveModuleState();
    m_constants = constants;
    lastAngle = waitForCANcoder();
    resetToAbsolute();
  }

  public SwerveModule(
      int moduleNumber,
      SwerveConstants constants,
      MotorType driveType,
      MotorConfiguration driveConfig,
      MotorType angleType,
      MotorConfiguration angleConfig) {
    m_moduleNumber = moduleNumber;
    m_driveMotor = MotorFactory.createMotor(driveType, driveConfig);
    m_angleMotor = MotorFactory.createMotor(angleType, angleConfig);
    setpoint = new SwerveModuleState();
    m_constants = constants;
    lastAngle = waitForCANcoder();
    resetToAbsolute();
  }

  public void setDesiredState(SwerveModuleState desiredState, boolean isOpenLoop) {
    /*
     * This is a custom optimize function, since default WPILib optimize assumes
     * continuous controller which CTRE and Rev onboard is not
     */
    desiredState = ModuleState.optimize(desiredState, getState().angle);

    setAngle(desiredState);
    setSpeed(desiredState, isOpenLoop);
  }

  private void setSpeed(SwerveModuleState desiredState, boolean isOpenLoop) {
    if (isOpenLoop) {
      double percentOutput = desiredState.speedMetersPerSecond / this.m_constants.maxSpeed;
      m_driveMotor.setPercentOut(percentOutput);
    } else {
      m_driveMotor.setRPM(
          Conversions.MPSToRPS(
                  desiredState.speedMetersPerSecond,
                  this.m_constants.wheelCircumference,
                  this.m_constants.driveGearRatio)
              * 60);
    }
  }

  private void setAngle(SwerveModuleState desiredState) {
    Rotation2d angle =
        (Math.abs(desiredState.speedMetersPerSecond) <= (this.m_constants.maxSpeed * 0.01))
            ? lastAngle
            : desiredState
                .angle; // Prevent rotating module if speed is less then 1%. Prevents Jittering.
    System.out.println("mod " + m_moduleNumber + " " + angle.getDegrees());
    m_angleMotor.setPosition(Conversions.degreesToRotations(angle.getDegrees(), this.m_constants.angleGearRatio));
    lastAngle = angle;
  }

  public Rotation2d getAngle() {
    return Rotation2d.fromDegrees(m_angleMotor.getPosition());
  }

  public Rotation2d getCanCoder() {
    return Rotation2d.fromRotations(m_angleEncoder.getAbsolutePosition().getValueAsDouble());
  }

  // only different method between talon and spark swerve drive
  private Rotation2d waitForCANcoder() {
    /* wait for up to 250ms for a new CANcoder position */
    return Rotation2d.fromRotations(
        m_angleEncoder.getAbsolutePosition().waitForUpdate(250).getValue());
  }

  public void resetToAbsolute() {
    // this is the issue. We did not wait for the CANCoder and the method takes
    // degrees EVEN THOUGH IT SAYS ROTATIONS
    // I HATE REVLIB
    double absolutePosition =
        waitForCANcoder().getDegrees() - m_constants.angleOffsets[m_moduleNumber];
    m_angleMotor.resetPosition(Rotation2d.fromDegrees(absolutePosition).getRotations());
  }

  // velocity (RPM -> Meters Per Second) / angle
  public SwerveModuleState getState() {
    return new SwerveModuleState(
        m_driveMotor.getRPM() / m_constants.driveGearRatio * m_constants.wheelCircumference / 60,
        getAngle());
  }

  // position (Rotations -> Meters driven) / angle
  public SwerveModulePosition getPosition() {
    return new SwerveModulePosition(
        m_driveMotor.getPosition()
            / m_constants.driveGearRatio
            * m_constants.wheelCircumference
            / 60,
        getAngle());
  }
}
