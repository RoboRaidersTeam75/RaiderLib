package RaiderLib.Subsystems.Shooters;

import RaiderLib.Config.MotorConfiguration;
import RaiderLib.Drivers.Motors.Motor;
import RaiderLib.Drivers.Motors.Motor.MotorType;
import RaiderLib.Drivers.Motors.MotorFactory;
import RaiderLib.Logging.Logger;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class DoubleFlyWheelShooter extends SubsystemBase {
  private Motor m_wheel1;
  private Motor m_wheel2;

  public DoubleFlyWheelShooter(
      MotorType m1Type,
      MotorType m2Type,
      MotorConfiguration m1Config,
      MotorConfiguration m2Config) {
    m_wheel1 = MotorFactory.createMotor(m1Type, m1Config);
    m_wheel2 = MotorFactory.createMotor(m2Type, m2Config);
    m_wheel1.setFollower(m_wheel2);
  }

  public void setRPM(double rpm) {
    m_wheel1.setRPM(rpm);
  }

  public void setPercent(double percent) {
    m_wheel1.setPercentOut(percent);
  }

  public void setVoltage(double voltage) {
    m_wheel1.setVoltage(voltage);
  }

  public double getRPM() {
    return m_wheel1.getRPM();
  }

  @Override
  public void periodic() {
    Logger.log("Shooter speed", getRPM());
    // SmartDashboard.putNumber("Shooter speed", getRPM());
  }
}
