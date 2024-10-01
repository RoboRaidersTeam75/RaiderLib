package RaiderLib.Config;

import RaiderLib.Drivers.Motors.Motor;
import RaiderLib.Drivers.Motors.Motor.NeutralMode;

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
    public PIDConstants slot0Configs = new PIDConstants();
    public PIDConstants slot1Configs = new PIDConstants();
    public PIDConstants slot2Configs = new PIDConstants();
    private PIDConstants nullConfig = new PIDConstants(0, 0, 0, 0);

    public boolean isConfigured() {
      if (slot0Configs.equals(nullConfig)) {
        return false;
      } else {
        return true;
      }
    }
  }

  public class GeneralConfigs {
    public boolean motorInvert = false;
    public NeutralMode neutralMode = NeutralMode.BRAKE;
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

  public MotorConfiguration setCANID(int CANID) {
    this.CANID = CANID;
    return this;
  }

  public MotorConfiguration setCanbus(String canbus) {
    this.canbus = canbus;
    return this;
  }

  public MotorConfiguration setSupplyCurrentLimit(int supplyCurrentLimit) {
    this.currentConfigs.SupplyCurrentLimit = supplyCurrentLimit;
    return this;
  }

  public MotorConfiguration setSupplyCurrentThresholdAmps(double supplyCurrentThresholdAmps) {
    this.currentConfigs.SupplyCurrentThresholdAmps = supplyCurrentThresholdAmps;
    return this;
  }

  public MotorConfiguration setSupplyCurrentThresholdSeconds(double supplyCurrentThresholdSeconds) {
    this.currentConfigs.SupplyCurrentThresholdSeconds = supplyCurrentThresholdSeconds;
    return this;
  }

  public MotorConfiguration setStatorCurrentLimit(int statorCurrentLimit) {
    this.currentConfigs.StatorCurrentLimit = statorCurrentLimit;
    return this;
  }


  public MotorConfiguration setPID(PIDConstants constants) {
    this.PIDConfigs.slot0Configs = constants;
    return this;
  }

  public MotorConfiguration setPIDSlot0(PIDConstants constants) {
    this.PIDConfigs.slot0Configs = constants;
    return this;
  }

  public MotorConfiguration setPIDSlot1(PIDConstants constants) {
    this.PIDConfigs.slot1Configs = constants;
    return this;
  }

  public MotorConfiguration setPIDSlot2(PIDConstants constants) {
    this.PIDConfigs.slot2Configs = constants;
    return this;
  }

  public MotorConfiguration setMotorInvert(boolean motorInvert) {
    this.generalConfigs.motorInvert = motorInvert;
    return this;
  }

  public MotorConfiguration setNeutralMode(NeutralMode neutralMode) {
    this.generalConfigs.neutralMode = neutralMode;
    return this;
  }

  public MotorConfiguration setSensorToMechanismRatio(double sensorToMechanismRatio) {
    this.generalConfigs.sensorToMechanismRatio = sensorToMechanismRatio;
    return this;
  }

  public MotorConfiguration setClosedLoopRampRateSeconds(double closedLoopRampRateSeconds) {
    this.rampRates.closedLoopRampRateSeconds = closedLoopRampRateSeconds;
    return this;
  }

  public MotorConfiguration setOpenLoopRampRateSeconds(double openLoopRampRateSeconds) {
    this.rampRates.openLoopRampRateSeconds = openLoopRampRateSeconds;
    return this;
  }

  public MotorConfiguration setForwardLimitPort(int forwardLimitPort) {
    this.hardwareLimits.forwardLimitPort = forwardLimitPort;
    return this;
  }

  public MotorConfiguration setReverseLimitPort(int reverseLimitPort) {
    this.hardwareLimits.reverseLimitPort = reverseLimitPort;
    return this;
  }

  public MotorConfiguration setEnableForwardLimit(boolean enableForwardLimit) {
    this.hardwareLimits.enableForwardLimit = enableForwardLimit;
    return this;
  }

  public MotorConfiguration setEnableReverseLimit(boolean enableReverseLimit) {
    this.hardwareLimits.enableReverseLimit = enableReverseLimit;
    return this;
  }

  public MotorConfiguration setEnableForwardSoftLimit(boolean enableForwardSoftLimit) {
    this.softLimits.enableForwardSoftLimit = enableForwardSoftLimit;
    return this;
  }

  public MotorConfiguration setEnableReverseSoftLimit(boolean enableReverseSoftLimit) {
    this.softLimits.enableReverseSoftLimit = enableReverseSoftLimit;
    return this;
  }

  public MotorConfiguration setForwardSoftLimitRotations(double forwardSoftLimitRotations) {
    this.softLimits.forwardSoftLimitRotations = forwardSoftLimitRotations;
    return this;
  }

  public MotorConfiguration setReverseSoftLimitRotations(double reverseSoftLimitRotations) {
    this.softLimits.reverseSoftLimitRotations = reverseSoftLimitRotations;
    return this;
  }




}
