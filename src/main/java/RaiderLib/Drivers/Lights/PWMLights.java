package RaiderLib.Drivers.Lights;

import RaiderLib.Config.LightsConstants;
import edu.wpi.first.wpilibj.AddressableLED;
import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.util.Color;

public abstract class PWMLights implements Lights {
  private final AddressableLED m_Leds;
  private AddressableLEDBuffer buffer;

  public PWMLights() {
    m_Leds = new AddressableLED(0);
  }

}