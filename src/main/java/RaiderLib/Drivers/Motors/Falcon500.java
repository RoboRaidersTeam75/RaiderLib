package RaiderLib.Drivers.Motors;

import RaiderLib.Config.MotorConfiguration;
import RaiderLib.Config.PIDConstants;
import com.ctre.phoenix6.configs.SlotConfigs;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.controls.DutyCycleOut;
import com.ctre.phoenix6.controls.PositionTorqueCurrentFOC;
import com.ctre.phoenix6.controls.PositionVoltage;
import com.ctre.phoenix6.controls.VelocityTorqueCurrentFOC;
import com.ctre.phoenix6.controls.VelocityVoltage;
import com.ctre.phoenix6.controls.VoltageOut;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.InvertedValue;
import com.ctre.phoenix6.signals.NeutralModeValue;

public class Falcon500 implements Motor {
  /** PID SLOTS: slot 0 is velocity slot 1 is position */
  private final TalonFX m_Falcon500;

  private Motor m_follower = null;

  private final MotorConfiguration m_Config;

  private final DutyCycleOut m_PercentOut = new DutyCycleOut(0);

  private final VoltageOut m_SysIDRequest = new VoltageOut(0);
  // for non phoenix pro enabled devices
  private final VelocityVoltage m_VelocityOut = new VelocityVoltage(0).withSlot(0);
  private final PositionVoltage m_PositionVoltage = new PositionVoltage(0).withSlot(1);

  // for phoenix pro enabled devices
  private final VelocityTorqueCurrentFOC m_VelocityProOut =
      new VelocityTorqueCurrentFOC(0).withSlot(0);
  private final PositionTorqueCurrentFOC m_PositionProOut =
      new PositionTorqueCurrentFOC(0).withSlot(1);
  private final boolean isLicensed;

  public Falcon500(MotorConfiguration config) {
    if (config.canbus != null) {
      m_Falcon500 = new TalonFX(config.CANID, config.canbus);
    } else {
      m_Falcon500 = new TalonFX(config.CANID);
    }
    m_Config = config;
    configMotor(config);
    m_Falcon500.setPosition(0);
    isLicensed = m_Falcon500.getIsProLicensed().waitForUpdate(5).getValue();
  }

  public MotorConfiguration getConfiguration() {
    return m_Config;
  }

  public void setPercentOut(double speed) {
    if (isLicensed) {
      m_Falcon500.setControl(m_PercentOut.withOutput(speed).withEnableFOC(true));
    } else {
      m_Falcon500.setControl(m_PercentOut.withOutput(speed).withEnableFOC(false));
    }
    if (m_follower != null) {
      m_follower.setPercentOut(speed);
    }
  }

  public void setRPM(double RPM) {
    if (isLicensed) {
      m_Falcon500.setControl(m_VelocityProOut.withVelocity(RPM));
    } else {
      m_Falcon500.setControl(m_VelocityOut.withVelocity(RPM).withEnableFOC(false));
    }
    if (m_follower != null) {
      m_follower.setRPM(RPM);
    }
  }

  public void setPosition(double Rotations) {
    if (isLicensed) {
      m_Falcon500.setControl(m_PositionProOut.withPosition(Rotations));
    } else {
      m_Falcon500.setControl(m_PositionVoltage.withPosition(Rotations).withEnableFOC(false));
    }
    if (m_follower != null) {
      m_follower.setPosition(Rotations);
    }
  }

  public void setVoltage(double voltage) {
    if (isLicensed) {
      m_Falcon500.setControl(m_SysIDRequest.withOutput(voltage).withEnableFOC(true));
    } else {
      m_Falcon500.setControl(m_SysIDRequest.withOutput(voltage).withEnableFOC(false));
    }
    if (m_follower != null) {
      m_follower.setVoltage(voltage);
    }
  }

  public double getRPM() {
    return m_Falcon500.getVelocity().refresh().getValue();
  }

  public double getPosition() {
    return m_Falcon500.getPosition().refresh().getValue();
  }

  public double getVoltage() {
    return m_Falcon500.getMotorVoltage().refresh().getValue();
  }

  public double getCurrent() {
    return m_Falcon500.getSupplyCurrent().refresh().getValue();
  }

  public void setPIDs(PIDConstants constants, int slot) {
    SlotConfigs config = new SlotConfigs();
    config.SlotNumber = slot;
    config.kP = constants.kP;
    config.kI = constants.kI;
    config.kD = constants.kD;
    config.kS = constants.kF; // feedforward overcomes static friction in this case
    m_Falcon500.getConfigurator().apply(config);
  }

  public void setP(double p, int slot) {
    SlotConfigs config = new SlotConfigs();
    config.SlotNumber = slot;
    config.kP = p;
    config.kI = 0.0;
    config.kD = 0.0;
    config.kS = 0.0;
    m_Falcon500.getConfigurator().apply(config);
  }

  public void setI(double i, int slot) {
    SlotConfigs config = new SlotConfigs();
    config.SlotNumber = slot;
    config.kP = 0.0;
    config.kI = i;
    config.kD = 0.0;
    config.kS = 0.0;
    m_Falcon500.getConfigurator().apply(config);
  }

  public void setD(double d, int slot) {
    SlotConfigs config = new SlotConfigs();
    config.SlotNumber = slot;
    config.kP = 0.0;
    config.kI = 0.0;
    config.kD = d;
    config.kS = 0.0;
    m_Falcon500.getConfigurator().apply(config);
  }

  public void setF(double f, int slot) {
    SlotConfigs config = new SlotConfigs();
    config.SlotNumber = slot;
    config.kP = 0.0;
    config.kI = 0.0;
    config.kD = 0.0;
    config.kS = f;
    m_Falcon500.getConfigurator().apply(config);
  }

  public MotorType getType() {
    return MotorType.FALCON500;
  }

  public void setFollower(Motor follower) {
    m_follower = follower;
  }

  private void configMotor(MotorConfiguration config) {
    TalonFXConfiguration falconConfig = new TalonFXConfiguration();

    falconConfig.ClosedLoopRamps.DutyCycleClosedLoopRampPeriod =
        config.rampRates.closedLoopRampRateSeconds;
    falconConfig.OpenLoopRamps.DutyCycleOpenLoopRampPeriod =
        config.rampRates.openLoopRampRateSeconds;

    falconConfig.CurrentLimits.StatorCurrentLimit = config.currentConfigs.StatorCurrentLimit;
    falconConfig.CurrentLimits.SupplyCurrentLimit = config.currentConfigs.SupplyCurrentLimit;
    falconConfig.CurrentLimits.SupplyCurrentThreshold =
        config.currentConfigs.SupplyCurrentThresholdAmps;
    falconConfig.CurrentLimits.SupplyTimeThreshold =
        config.currentConfigs.SupplyCurrentThresholdSeconds;
    falconConfig.CurrentLimits.StatorCurrentLimitEnable = true;
    falconConfig.CurrentLimits.SupplyCurrentLimitEnable = true;

    falconConfig.Feedback.SensorToMechanismRatio = config.generalConfigs.sensorToMechanismRatio;
    falconConfig.MotorOutput.Inverted =
        config.generalConfigs.motorInvert
            ? InvertedValue.Clockwise_Positive
            : InvertedValue.CounterClockwise_Positive;
    falconConfig.MotorOutput.NeutralMode =
        config.generalConfigs.brakeModeEnabled ? NeutralModeValue.Brake : NeutralModeValue.Coast;

    falconConfig.SoftwareLimitSwitch.ForwardSoftLimitThreshold =
        config.softLimits.forwardSoftLimitRotations;
    falconConfig.SoftwareLimitSwitch.ReverseSoftLimitThreshold =
        config.softLimits.forwardSoftLimitRotations;
    falconConfig.SoftwareLimitSwitch.ForwardSoftLimitEnable =
        config.softLimits.enableForwardSoftLimit;
    falconConfig.SoftwareLimitSwitch.ReverseSoftLimitEnable =
        config.softLimits.enableForwardSoftLimit;

    falconConfig.Slot0.kP = config.PIDConfigs.slot0Configs.kP;
    falconConfig.Slot0.kI = config.PIDConfigs.slot0Configs.kI;
    falconConfig.Slot0.kD = config.PIDConfigs.slot0Configs.kD;
    falconConfig.Slot0.kS = config.PIDConfigs.slot0Configs.kF;

    falconConfig.Slot1.kP = config.PIDConfigs.slot1Configs.kP;
    falconConfig.Slot1.kI = config.PIDConfigs.slot1Configs.kI;
    falconConfig.Slot1.kD = config.PIDConfigs.slot1Configs.kD;
    falconConfig.Slot1.kS = config.PIDConfigs.slot1Configs.kF;

    falconConfig.Slot2.kP = config.PIDConfigs.slot2Configs.kP;
    falconConfig.Slot2.kI = config.PIDConfigs.slot2Configs.kI;
    falconConfig.Slot2.kD = config.PIDConfigs.slot2Configs.kD;
    falconConfig.Slot2.kS = config.PIDConfigs.slot2Configs.kF;
  }
}
