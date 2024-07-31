package RaiderLib.Drivers.IMUs;

enum IMUType {
  PIGEON2,
  NAVX
}

// TODO: implement
public class IMUFactory {
  public static IMU createIMU(IMUType type, int CANID) {
    switch (type) {
      case PIGEON2:
        return new Pigeon2(CANID);
      default:
        throw new IllegalArgumentException(
            "Illegal IMU Passed to Factory Method, You probably tried to create a NavX Motor and specified the CAN Bus");
    }
  }

  public static IMU createIMU(IMUType type) {
    switch (type) {
      case NAVX:
        return new NavX();
      default:
        throw new IllegalArgumentException(
            "Illegal IMU Passed to Factory Method, You probably tried to create a Pigeon Motor and didn't specify the CAN Bus");
    }
  }
}
