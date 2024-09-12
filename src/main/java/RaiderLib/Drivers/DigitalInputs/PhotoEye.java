package RaiderLib.Drivers.DigitalInputs;

import edu.wpi.first.wpilibj.DigitalInput;

public abstract class PhotoEye {
  private DigitalInput m_photoEye;

  public PhotoEye(int port) {
    m_photoEye = new DigitalInput(port);
  }

  public boolean get() {
    return m_photoEye.get();
  }
}
