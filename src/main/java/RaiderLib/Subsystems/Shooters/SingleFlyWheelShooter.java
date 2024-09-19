package RaiderLib.Subsystems.Shooters;

import RaiderLib.Config.MotorConfiguration;
import RaiderLib.Drivers.Motors.Motor;
import RaiderLib.Drivers.Motors.Motor.MotorType;
import RaiderLib.Drivers.Motors.MotorFactory;
import RaiderLib.Logging.Logger;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public abstract class SingleFlyWheelShooter extends SubsystemBase {
  private Motor m_wheel;

  /**
   * PIDS need to be set for this subsystem to work TODO: throw an error if pids are not configured
   * TODO: Smartdashboard tabs integration
   *
   * @param type the motor type
   * @param config the config object for the motor
   */
  public SingleFlyWheelShooter(MotorType type, MotorConfiguration config) {
    m_wheel = MotorFactory.createMotor(type, config);
  }

  public void setRPM(double rpm) {
    m_wheel.setRPM(rpm);
  }

  public void setPercent(double percent) {
    m_wheel.setPercentOut(percent);
  }

  public void setVoltage(double voltage) {
    m_wheel.setVoltage(voltage);
  }

  public double getRPM() {
    return m_wheel.getRPM();
  }

  @Override
  public void periodic() {
    Logger.log("Single flywheel shooter speed", getRPM());
  }
}
