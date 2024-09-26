package RaiderLib.Drivers.IMUs;

import edu.wpi.first.math.geometry.Rotation2d;

public class Pigeon2 implements IMU {

  private com.ctre.phoenix6.hardware.Pigeon2
      pigeon; // this is what we get for naming the class the same as in the

  // library

  public Pigeon2(int CANID, String canBus) {
    pigeon = new com.ctre.phoenix6.hardware.Pigeon2(CANID, canBus);
  }

  public double getAngle() {
    return pigeon.getAngle();
  }

  public double getRoll() {
    return pigeon.getRoll().getValueAsDouble();
  }

  public double getPitch() {
    return pigeon.getPitch().getValueAsDouble();
  }

  public double getYaw() {
    return pigeon.getYaw().getValueAsDouble();
  }

  public Rotation2d getRotation2d() {
    return Rotation2d.fromDegrees(pigeon.getYaw().getValueAsDouble());
  }

  public void setAngle(Rotation2d angle) {
    pigeon.setYaw(angle.getDegrees());
  }

  public void reset() {
    pigeon.setYaw(0);
  }
}
