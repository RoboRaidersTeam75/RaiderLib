// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package RaiderLib.Subsystems.Shooters;

import RaiderLib.Config.MotorConfiguration;
import RaiderLib.Drivers.DigitalInputs.LimitSwitch;
import RaiderLib.Drivers.Motors.Motor;
import RaiderLib.Drivers.Motors.Motor.MotorType;
import RaiderLib.Drivers.Motors.MotorFactory;
import RaiderLib.Logging.Logger;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class HorizontalTurret extends SubsystemBase {

  public enum LimitType {
    HARD,
    SEMISOFT
  };

  private Motor m_angler;
  private LimitSwitch m_leftLimit;
  private LimitSwitch m_rightLimit;
  private LimitType m_limitType;

  public HorizontalTurret(
      MotorType motorType, MotorConfiguration motorConfig, LimitType limitType, LimitSwitch limit) {
    if (limitType == LimitType.HARD) {
      m_angler = null;
      Logger.log("Turret limit error", "Limit switches not provided");
      System.out.println(
          "TURRET LIMIT ERROR: must provide two limits switches when using hard limits");
      return;
    }
    m_angler = MotorFactory.createMotor(motorType, motorConfig);
    m_leftLimit = limit;
    m_limitType = limitType;
  }

  public HorizontalTurret(
      MotorType motorType,
      MotorConfiguration motorConfig,
      LimitType limitType,
      LimitSwitch limit1,
      LimitSwitch limit2) {
    if (limitType == LimitType.SEMISOFT) {
      m_angler = null;
      Logger.log("Turret limit error", "Extra limit switch provided");
      System.out.println("Two limit switches provided, second one will be ignored");
    }
    m_angler = MotorFactory.createMotor(motorType, motorConfig);
    m_leftLimit = limit1;
    m_rightLimit = limit2;
    m_limitType = limitType;
  }

  public void setAngle(double degrees, double speed) {
    double threshold = 0.01;
    if (degrees / 360 > m_angler.getPosition()) {
      speed = -speed;
    }
    while (!MathUtil.isNear(degrees / 360, m_angler.getPosition(), threshold)) {
      if (m_limitType == LimitType.HARD && (m_leftLimit.get() || m_rightLimit.get())) {
        break;
      }
      // if (m_limitType == LimitType.SEMISOFT && m_leftLimit.get())
      setSpeed(speed);
    }
    setSpeed(0);
  }

  public void setSpeed(double speed) { // percent
    m_angler.setRPM(speed);
  }

  public void resetAngle() {
    m_angler.resetPosition(0);
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }
}
