package RaiderLib.Drivers.IMUs;

import edu.wpi.first.math.geometry.Rotation2d;

public interface IMU {

  public double getAngle();

  public double getYaw();

  public double getRoll();

  public double getPitch();

  public Rotation2d getRotation2d();

  public void setAngle(Rotation2d angle);
}
