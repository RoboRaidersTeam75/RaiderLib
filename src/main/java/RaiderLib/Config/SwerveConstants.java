package RaiderLib.Config;

import edu.wpi.first.math.kinematics.SwerveDriveKinematics;

public class SwerveConstants {
  public double trackWidth = 0;
  public double wheelBase = 0;

  public double wheelCircumference = 0;

  public double driveGearRatio = 0;
  public double angleGearRatio = 0;

  public double maxSpeed = 0;

  public double[] angleOffsets;

  public SwerveDriveKinematics kinematics;

  public SwerveConstants() {}

  public SwerveConstants setTrackWidth(double trackWidth) {
    this.trackWidth = trackWidth;
    return this;
  }

  public SwerveConstants setWheelBase(double wheelBase) {
    this.wheelBase = wheelBase;
    return this;
  }

  public SwerveConstants setWheelCircumference(double wheelCircumference) {
    this.wheelCircumference = wheelCircumference;
    return this;
  }

  public SwerveConstants setDriveGearRatio(double driveGearRatio) {
    this.driveGearRatio = driveGearRatio;
    return this;
  }

  public SwerveConstants setAngleGearRatio(double angleGearRatio) {
    this.angleGearRatio = angleGearRatio;
    return this;
  }

  public SwerveConstants setMaxSpeed(double maxSpeed) {
    this.maxSpeed = maxSpeed;
    return this;
  }

  public SwerveConstants setKinematics(SwerveDriveKinematics kinematics) {
    this.kinematics = kinematics;
    return this;
  }

  public SwerveConstants setAngleOffsets(double[] offsets) {
    this.angleOffsets = offsets;
    return this;
  }
}
