package RaiderLib.Subsystems.Drivetrains;

import RaiderLib.Config.MotorConfiguration;
import RaiderLib.Drivers.Motors.Motor;
import RaiderLib.Drivers.Motors.Motor.MotorType;
import RaiderLib.Drivers.Motors.MotorFactory;
import RaiderLib.Util.Conversions;
import RaiderLib.Util.ModuleState;
import RaiderLib.Util.SwerveConstants;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;

public class SwerveModule {
  public int m_moduleNumber;

  private Motor m_driveMotor;
  private Motor m_angleMotor;

  private Rotation2d lastAngle;

  private SwerveModuleState setpoint;

  private SwerveConstants m_constants;

  public SwerveModule(
      int moduleNumber,
      SwerveConstants constants,
      MotorType type,
      MotorConfiguration driveConfig,
      MotorConfiguration angleConfig) {
    m_moduleNumber = moduleNumber;
    m_driveMotor = MotorFactory.createMotor(type, driveConfig);
    m_angleMotor = MotorFactory.createMotor(type, angleConfig);
    setpoint = new SwerveModuleState();
    m_constants = constants;
    lastAngle = getAngle();
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
    lastAngle = getAngle();
  }

  public void setDesiredState(SwerveModuleState desiredState) {
    desiredState = ModuleState.optimize(desiredState, getAngle());
    setpoint = desiredState;

    // TODO: ADD CLOSED LOOP FOR DRIVE MOTOR
    m_driveMotor.setPercentOut(desiredState.speedMetersPerSecond / m_constants.maxSpeed);

    Rotation2d angle =
        (Math.abs(desiredState.speedMetersPerSecond) <= (m_constants.maxSpeed * 0.01))
            ? lastAngle
            : desiredState
                .angle; // Prevent rotating module if speed is less then 1%. Prevents Jittering.

    m_angleMotor.setPosition(
        Conversions.degreesToRotations(angle.getDegrees(), m_constants.angleGearRatio));
    lastAngle = angle;
  }

  public Rotation2d getAngle() {
    return Rotation2d.fromDegrees(
        Conversions.rotationsToDegrees(m_angleMotor.getPosition(), m_constants.angleGearRatio));
  }

  public SwerveModuleState getState() {
    // TODO: again, lack of SwerveConstants is stopping us from being able to do this
    // (driveGearRatio, wheelCircumference)
    return new SwerveModuleState(
        Conversions.RPSToMPS(
            m_driveMotor.getRPM(), m_constants.wheelCircumference, m_constants.driveGearRatio),
        getAngle());
  }

  public SwerveModulePosition getPosition() {
    return new SwerveModulePosition(
        Conversions.rotationsToMeters(
            m_driveMotor.getPosition(), m_constants.wheelCircumference, m_constants.driveGearRatio),
        getAngle());
  }
}
