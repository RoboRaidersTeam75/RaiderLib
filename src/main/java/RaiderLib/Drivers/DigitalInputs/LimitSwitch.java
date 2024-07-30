package RaiderLib.Drivers.DigitalInputs;

import edu.wpi.first.wpilibj.DigitalInput;

public abstract class LimitSwitch {
  private DigitalInput m_limitSwitch;
  
  public LimitSwitch(int port) {
    m_limitSwitch = new DigitalInput(port);
  }

  public boolean get() {
    return m_limitSwitch.get();
  }
}
