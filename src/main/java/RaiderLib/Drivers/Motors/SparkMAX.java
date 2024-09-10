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
    configMotor(config);
  }

  public MotorConfiguration getConfiguration() {
    return m_Config;
  }

  public void setPercentOut(double speed) {
    m_SparkMAX.set(speed);
    m_follower.setPercentOut(speed);
  }

  public void setRPM(double RPM) {
    m_Controller.setReference(RPM, ControlType.kVelocity);
    m_follower.setRPM(RPM);
  }

  public void setPosition(double Rotations) {
    m_Controller.setReference(Rotations, ControlType.kPosition);
    m_follower.setPosition(Rotations);
  }

  public void setVoltage(double voltage) {
    m_SparkMAX.setVoltage(voltage);
    m_follower.setVoltage(voltage);
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

  private void configMotor(MotorConfiguration config) {
    m_SparkMAX.setIdleMode(
        config.generalConfigs.brakeModeEnabled ? IdleMode.kBrake : IdleMode.kCoast);
    m_SparkMAX.setInverted(config.generalConfigs.motorInvert);

    m_SparkMAX.setOpenLoopRampRate(config.rampRates.openLoopRampRateSeconds);
    m_SparkMAX.setClosedLoopRampRate(config.rampRates.closedLoopRampRateSeconds);
    m_SparkMAX.setSmartCurrentLimit(config.currentConfigs.StatorCurrentLimit);
    m_SparkMAX.setSecondaryCurrentLimit(config.currentConfigs.SupplyCurrentLimit);
    m_SparkMAX.setSoftLimit(
        SoftLimitDirection.kForward, (float) config.softLimits.forwardSoftLimitRotations);
    m_SparkMAX.setSoftLimit(
        SoftLimitDirection.kReverse, (float) config.softLimits.reverseSoftLimitRotations);

    m_Encoder.setPosition(0);
    m_Encoder.setPositionConversionFactor(config.generalConfigs.sensorToMechanismRatio);
    m_Encoder.setVelocityConversionFactor(config.generalConfigs.sensorToMechanismRatio);

    m_Controller.setP(config.PIDConfigs.slot0Configs.kP, 0);
    m_Controller.setI(config.PIDConfigs.slot0Configs.kI, 0);
    m_Controller.setD(config.PIDConfigs.slot0Configs.kD, 0);
    m_Controller.setFF(config.PIDConfigs.slot0Configs.kF, 0);

    m_Controller.setP(config.PIDConfigs.slot1Configs.kP, 1);
    m_Controller.setI(config.PIDConfigs.slot1Configs.kI, 1);
    m_Controller.setD(config.PIDConfigs.slot1Configs.kD, 1);
    m_Controller.setFF(config.PIDConfigs.slot1Configs.kF, 1);

    m_Controller.setP(config.PIDConfigs.slot2Configs.kP, 2);
    m_Controller.setI(config.PIDConfigs.slot2Configs.kI, 2);
    m_Controller.setD(config.PIDConfigs.slot2Configs.kD, 2);
    m_Controller.setFF(config.PIDConfigs.slot2Configs.kF, 2);
  }
}
