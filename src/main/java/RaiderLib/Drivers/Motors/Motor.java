package RaiderLib.Drivers.Motors;

import RaiderLib.Config.MotorConfiguration;
import RaiderLib.Config.PIDConstants;

public interface Motor {

  public enum MotorType {
    FALCON500,
    KRAKENX60,
    CANSPARKMAX,
    CANSPARKFLEX
  }

  /** gets motor configuration */
  public MotorConfiguration getConfiguration();

  /**
   * sets the speed from -100% to 100%
   *
   * @param speed - a number from -1 to 1
   */
  public void setPercentOut(double speed);

  /**
   * sets the RPM of the motor, in rotations of the target shaft (conversion factor honored)
   *
   * @param RPM - the desired RPM
   */
  public void setRPM(double RPM);

  /**
   * sets the position, in rotations of the target shaft (conversion factor honored)
   *
   * @param Rotations
   */
  public void setPosition(double Rotations);

  /**
   * sets the voltage 0-12 volts of the motor
   *
   * @param voltage - the voltage to be set
   */
  public void setVoltage(double voltage);

  /**
   * gets the RPM of the motor, in RPM of the target shaft (conversion factor honored)
   *
   * @return - the RPM of the motor
   */
  public double getRPM();

  /**
   * the position, in rotations of the target shaft, of the motor (conversion factor honored)
   *
   * @return - the rotations of the motor
   */
  public double getPosition();

  /**
   * how many volts are being passed through the motor
   *
   * @return - the voltage of the motor
   */
  public double getVoltage();

  /**
   * gets the current the motor is using from the supply side
   *
   * @return - the supply current in Amps
   */
  public double getCurrent();

  /**
   * configs all pids again if needed. This method should not be called often, if single terms need
   * to be changed, use the config[TERM] methods
   *
   * @param constants - the constants to set (P, I, D, F)
   */
  public void setPIDs(PIDConstants constants, int slot);

  /*
   * set Individual PID terms
   */
  /**
   * sets the P term
   *
   * @param p - proportional term
   */
  public void setP(double p, int slot);

  /**
   * sets the I term
   *
   * @param i - integral term
   */
  public void setI(double i, int slot);

  /**
   * sets the D term
   *
   * @param d - derivative term
   */
  public void setD(double d, int slot);

  /**
   * sets the F term
   *
   * @param f - feedforward term
   */
  public void setF(double f, int slot);

  public MotorType getType();

  public void setFollower(Motor follower);
}
