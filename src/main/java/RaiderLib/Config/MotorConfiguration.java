package RaiderLib.Config;

/*
 * current limits
 * time exceeding current limit
 * PIDS
 * Neutral mode
 * Invert
 * ramp rates
 */
public class MotorConfiguration {
  public int CANID;
  public String canbus = null; // should be kept unspecified if its a REV motor

  public class CurrentConfigs {
    public int SupplyCurrentLimit = 60;
    public double SupplyCurrentThresholdAmps = 80;
    public double SupplyCurrentThresholdSeconds = 0.2; // seconds above peak current
    public int StatorCurrentLimit = 40;
  }

  public class PIDConfigs {
    public class Slot0Configs {
      public double kP = 0;
      public double kI = 0;
      public double kD = 0;
      public double kF = 0;
    }

    public class Slot1Configs {
      public double kP = 0;
      public double kI = 0;
      public double kD = 0;
      public double kF = 0;
    }

    public class Slot2Configs {
      public double kP = 0;
      public double kI = 0;
      public double kD = 0;
      public double kF = 0;
    }

    public Slot0Configs slot0Configs = new Slot0Configs();
    public Slot1Configs slot1Configs = new Slot1Configs();
    public Slot2Configs slot2Configs = new Slot2Configs();
  }

  public class GeneralConfigs {
    public boolean motorInvert = false;
    public boolean brakeModeEnabled = false;
    public double sensorToMechanismRatio = (1.0 / 1.0); // conversion factor
  }

  public class RampRates {
    public double closedLoopRampRateSeconds = 0;
    public double openLoopRampRateSeconds = 0;
  }

  public class HardwareLimits {
    public int forwardLimitPort = 0; // RIO port;
    public int reverseLimitPort = 0; // RIO port;
    public boolean enableForwardLimit = false;
    public boolean enableReverseLimit = false;
  }

  public class SoftLimits {
    public boolean enableForwardSoftLimit = false;
    public boolean enableReverseSoftLimit = false;
    public double forwardSoftLimitRotations = 0;
    public double reverseSoftLimitRotations = 0;
  }

  public CurrentConfigs currentConfigs = new CurrentConfigs();
  public PIDConfigs PIDConfigs = new PIDConfigs();
  public GeneralConfigs generalConfigs = new GeneralConfigs();
  public RampRates rampRates = new RampRates();
  public HardwareLimits hardwareLimits = new HardwareLimits();
  public SoftLimits softLimits = new SoftLimits();

  public MotorConfiguration() {}
}
