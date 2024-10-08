package RaiderLib.Drivers.Motors;

import RaiderLib.Config.MotorConfiguration;
import RaiderLib.Config.PIDConstants;
import com.revrobotics.CANSparkBase.ControlType;
import com.revrobotics.CANSparkBase.IdleMode;
import com.revrobotics.CANSparkBase.SoftLimitDirection;
import com.revrobotics.CANSparkLowLevel;
import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkPIDController;

public class SparkMAX implements Motor {
  /*
   * slot 0 is Velocity
   * slot 1 is position
   */
  private final CANSparkMax m_SparkMAX;
  private Motor m_follower;
  private final SparkPIDController m_Controller;
  private final RelativeEncoder m_Encoder;
  private final MotorConfiguration m_Config;

  public SparkMAX(MotorConfiguration config) {
    m_SparkMAX = new CANSparkMax(config.CANID, CANSparkLowLevel.MotorType.kBrushless);
    m_Controller = m_SparkMAX.getPIDController();
    m_Encoder = m_SparkMAX.getEncoder();
    m_Config = config;
    configMotor();
  }

  public MotorConfiguration getConfiguration() {
    return m_Config;
  }

  // hi
  public void setPercentOut(double speed) {
    m_SparkMAX.set(speed);
    if (m_follower != null) {
      m_follower.setPercentOut(speed);
    }
  }

  public void setRPM(double RPM) {
    m_Controller.setReference(RPM, ControlType.kVelocity);
    if (m_follower != null) {
      m_follower.setRPM(RPM);
    }
  }

  public void setPosition(double Rotations) {
    m_Controller.setReference(Rotations, ControlType.kPosition);
    
    if (m_follower != null) {
      m_follower.setPosition(Rotations);
    }
  }

  public void setVoltage(double voltage) {
    m_SparkMAX.setVoltage(voltage);
    if (m_follower != null) {
      m_follower.setVoltage(voltage);
    }
  }

  public double getRPM() {
    return m_Encoder.getVelocity();
  }

  public double getPosition() {
    return m_Encoder.getPosition();
  }

  public double getVoltage() {
    return m_SparkMAX.getAppliedOutput() * m_SparkMAX.getBusVoltage();
  }

  public double getCurrent() {
    return m_SparkMAX.getOutputCurrent();
  }

  public void resetPosition(double value) {
    m_Encoder.setPosition(value);
  }

  public void setPIDs(PIDConstants constants, int slot) {
    m_Controller.setP(constants.kP, slot);
    m_Controller.setI(constants.kI, slot);
    m_Controller.setD(constants.kD, slot);
    m_Controller.setFF(constants.kF, slot);
  }

  public void setP(double p, int slot) {
    m_Controller.setP(p, slot);
  }

  public void setI(double i, int slot) {
    m_Controller.setI(i, slot);
  }

  public void setD(double d, int slot) {
    m_Controller.setD(d, slot);
  }

  public void setF(double f, int slot) {
    m_Controller.setFF(f, slot);
  }

  public MotorType getType() {
    return MotorType.CANSPARKMAX;
  }

  public void setFollower(Motor follower) {
    m_follower = follower;
  }

  public void configMotor() {
    m_SparkMAX.setIdleMode(
        this.m_Config.generalConfigs.neutralMode == NeutralMode.BRAKE
            ? IdleMode.kBrake
            : IdleMode.kCoast);
    m_SparkMAX.setInverted(this.m_Config.generalConfigs.motorInvert);

    m_SparkMAX.setOpenLoopRampRate(this.m_Config.rampRates.openLoopRampRateSeconds);
    m_SparkMAX.setClosedLoopRampRate(this.m_Config.rampRates.closedLoopRampRateSeconds);
    m_SparkMAX.setSmartCurrentLimit(this.m_Config.currentConfigs.StatorCurrentLimit);
    m_SparkMAX.setSecondaryCurrentLimit(this.m_Config.currentConfigs.SupplyCurrentLimit);
    m_SparkMAX.setSoftLimit(
        SoftLimitDirection.kForward, (float) this.m_Config.softLimits.forwardSoftLimitRotations);
    m_SparkMAX.setSoftLimit(
        SoftLimitDirection.kReverse, (float) this.m_Config.softLimits.reverseSoftLimitRotations);

    m_Encoder.setPosition(0);
    m_Encoder.setPositionConversionFactor(this.m_Config.generalConfigs.sensorToMechanismRatio);
    m_Encoder.setVelocityConversionFactor(this.m_Config.generalConfigs.sensorToMechanismRatio);

    m_Controller.setP(this.m_Config.PIDConfigs.slot0Configs.kP, 0);
    m_Controller.setI(this.m_Config.PIDConfigs.slot0Configs.kI, 0);
    m_Controller.setD(this.m_Config.PIDConfigs.slot0Configs.kD, 0);
    m_Controller.setFF(this.m_Config.PIDConfigs.slot0Configs.kF, 0);

    m_Controller.setP(this.m_Config.PIDConfigs.slot1Configs.kP, 1);
    m_Controller.setI(this.m_Config.PIDConfigs.slot1Configs.kI, 1);
    m_Controller.setD(this.m_Config.PIDConfigs.slot1Configs.kD, 1);
    m_Controller.setFF(this.m_Config.PIDConfigs.slot1Configs.kF, 1);

    m_Controller.setP(this.m_Config.PIDConfigs.slot2Configs.kP, 2);
    m_Controller.setI(this.m_Config.PIDConfigs.slot2Configs.kI, 2);
    m_Controller.setD(this.m_Config.PIDConfigs.slot2Configs.kD, 2);
    m_Controller.setFF(this.m_Config.PIDConfigs.slot2Configs.kF, 2);
  }
}
