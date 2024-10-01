// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj2.command.Command;
import java.util.function.DoubleSupplier;

import RaiderLib.Logging.Logger;
import RaiderLib.Subsystems.Drivetrains.SwerveDrive;

/** This is the main command to drive the robot */
public class TeleopSwerve extends Command {
  private SwerveDrive m_Swerve;
  private DoubleSupplier translationSup;
  private DoubleSupplier strafeSup;
  private DoubleSupplier rotationSup;
  private boolean isOpenLoop;

  private final double translationStickMapValue = 1.5;
  private final double rotationStickMapValue = 1.25;

  public static final double translationJoystickExpo = 1.46;

  public TeleopSwerve(
      SwerveDrive m_Swerve,
      DoubleSupplier translationSup,
      DoubleSupplier strafeSup,
      DoubleSupplier rotationSup,
      boolean isOpenLoop) {
    this.m_Swerve = m_Swerve;
    // get all values to drive the robot (x,y,z)
    this.translationSup = translationSup;
    this.strafeSup = strafeSup;
    this.rotationSup = rotationSup;
    this.isOpenLoop = isOpenLoop;

    addRequirements(m_Swerve);
  }

  @Override
  public void execute() {
    /* Get Values, Deadband */
    // add deadbands to prevent jittering on small stick inputs
    double translationVal =
        MathUtil.applyDeadband(translationSup.getAsDouble() * translationStickMapValue, .1);
    double strafeVal = MathUtil.applyDeadband(strafeSup.getAsDouble() * translationStickMapValue, .1);
    double rotationVal = MathUtil.applyDeadband(rotationSup.getAsDouble() * rotationStickMapValue, .1);

    translationVal = translationVal >= 0 ? Math.pow(translationVal, translationJoystickExpo) : -1 * Math.pow(-translationVal, translationJoystickExpo);
    strafeVal = strafeVal >= 0 ?  Math.pow(strafeVal, translationJoystickExpo) :  -1 * Math.pow(-strafeVal, translationJoystickExpo);

    // translationVal = Math.pow(translationVal, translationJoystickExpo) * Math.copySign(1.0,translationVal);
    // strafeVal = Math.pow(strafeVal, translationJoystickExpo) * Math.copySign(1.0,strafeVal);


    /*
     * make the translation to drive the robot
     * Multiply it by max speed as the drive command has units of meters per second
     */
    Translation2d translation2d =
        new Translation2d(translationVal, strafeVal).times(4.5);

    double wheelBase = 0.603;
    double trackWidth = 0.603;

    // drive the robot. Multiple the rotation value by 0.5 to make the rotation easier to handle
    m_Swerve.drive(
        translation2d, rotationVal * 6 / Math.hypot(trackWidth / 2.0, wheelBase / 2.0), isOpenLoop);
  }
}
