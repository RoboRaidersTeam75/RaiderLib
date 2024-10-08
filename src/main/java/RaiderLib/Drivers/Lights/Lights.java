// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package RaiderLib.Drivers.Lights;

import edu.wpi.first.wpilibj.util.Color;

/** Add your docs here. */
public interface Lights {

  public enum LightType {
    ADDRESSABLELED,
    CANDLE
  }

  /* Currently hardcoded for 2024 */
  public enum Section {
    NOTE,
    ALIGN
  }

  public void off(Section section);

  public void setColor(Section section);

  public void solid(Color color);

  public void blink(Color color, double interval, Section section);

  public void breathe(Color color, double interval, Section section);

  public void rainbow(double speed);

  public void wave(Color[] colors, double speed);
}
