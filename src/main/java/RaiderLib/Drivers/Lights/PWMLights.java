package RaiderLib.Drivers.Lights;

import RaiderLib.Config.LightsConstants;
import edu.wpi.first.wpilibj.AddressableLED;
import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.util.Color;

public abstract class PWMLights implements Lights {
  private final AddressableLED m_Leds;
  private final LightsConstants m_lightsConstants;

  private AddressableLEDBuffer buffer;

  public PWMLights(LightsConstants lightsConstants) {
    m_lightsConstants = lightsConstants;

    m_Leds = new AddressableLED(m_lightsConstants.LEDPWMPort);
    buffer = new AddressableLEDBuffer(m_lightsConstants.bufferLength);

    m_Leds.setLength(buffer.getLength());
    m_Leds.setData(buffer);
    m_Leds.start();
  }

  public void off() {

  }

}