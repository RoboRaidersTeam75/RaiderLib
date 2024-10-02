package RaiderLib.Subsystems.Drivetrains;

import com.ctre.phoenix6.hardware.CANcoder;

import RaiderLib.Config.MotorConfiguration;
import RaiderLib.Config.SwerveConstants;
import RaiderLib.Dashboard.TuningTab;
import RaiderLib.Drivers.Motors.Motor;
import RaiderLib.Drivers.Motors.Motor.MotorType;
import RaiderLib.Drivers.Motors.MotorFactory;
import RaiderLib.Util.Conversions;
import RaiderLib.Util.ModuleState;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class SwerveModule {
  public int m_moduleNumber;

  private Motor m_driveMotor;
  private Motor m_angleMotor;
  private CANcoder m_angleEncoder;

  private Rotation2d lastAngle;

  private SwerveModuleState setpoint;

  private SwerveConstants m_constants;

    public SwerveModule(
      int moduleNumber,
      SwerveConstants constants,
      MotorType type,
      MotorConfiguration driveConfig,
      MotorConfiguration angleConfig
      ) {
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

  public void setDesiredState(SwerveModuleState desiredState) {
    desiredState = ModuleState.optimize(desiredState, waitForCANcoder());
    setpoint = desiredState;

    // TODO: ADD CLOSED LOOP FOR DRIVE MOTOR
    m_driveMotor.setPercentOut(desiredState.speedMetersPerSecond / m_constants.maxSpeed);

    Rotation2d angle =
        (Math.abs(desiredState.speedMetersPerSecond) <= (m_constants.maxSpeed * 0.01))
            ? lastAngle
            : desiredState.angle; // Prevent rotating module if speed is less then 1%. Prevents Jittering.

    m_angleMotor.setPosition(angle.getRotations() * m_constants.angleGearRatio);
    lastAngle = angle;
  }

  public Rotation2d getCANCoder() {
    return Rotation2d.fromRotations(m_angleEncoder.getAbsolutePosition().getValue());
  }

  /**
   * @return wait for thee CANCoder due to phoenix 6 status signals
   */
  private Rotation2d waitForCANcoder() {
    /* wait for up to 250ms for a new CANcoder position */
    return Rotation2d.fromRotations(
        m_angleEncoder.getAbsolutePosition().waitForUpdate(250).getValue());
  }

  public void resetToAbsolute() {
    double absolutePosition = (getCANCoder().getRotations() - m_constants.angleOffsets[m_moduleNumber] / 360.0);
    m_angleMotor.resetPosition(absolutePosition);
  }


  public SwerveModuleState getState() {
    // TODO: again, lack of SwerveConstants is stopping us from being able to do this
    // (driveGearRatio, wheelCircumference)
    return new SwerveModuleState(
        Conversions.RPSToMPS(
            m_driveMotor.getRPM(), m_constants.wheelCircumference, m_constants.driveGearRatio),
        waitForCANcoder());
  }

  public SwerveModulePosition getPosition() {
    return new SwerveModulePosition(
        Conversions.rotationsToMeters(
            m_driveMotor.getPosition(), m_constants.wheelCircumference, m_constants.driveGearRatio),
        waitForCANcoder());
  }
}
