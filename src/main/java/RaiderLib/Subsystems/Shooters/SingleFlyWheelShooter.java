package RaiderLib.Subsystems.Shooters;

import RaiderLib.Config.MotorConfiguration;
import RaiderLib.Drivers.Motors.Motor;
import RaiderLib.Drivers.Motors.Motor.MotorType;
import RaiderLib.Drivers.Motors.MotorFactory;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public abstract class SingleFlyWheelShooter extends SubsystemBase {
  private final Motor m_Flywheel;

  /**
   * PIDS need to be set for this subsystem to work TODO: throw an error if pids are not configured
   * TODO: Smartdashboard tabs integration
   *
   * @param type the motor type
   * @param config the config object for the motor
   */
  public SingleFlyWheelShooter(MotorType type, MotorConfiguration config) {
    m_Flywheel = MotorFactory.createMotor(type, config);
  }

  public void setRPM(double RPM) {
    m_Flywheel.setRPM(RPM);
  }

  public double getRPM() {
    return m_Flywheel.getRPM();
  }

  @Override
  public void periodic() {
    // TODO: logger addition
  }
}
