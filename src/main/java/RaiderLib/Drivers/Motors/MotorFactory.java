package RaiderLib.Drivers.Motors;

import RaiderLib.Config.MotorConfiguration;
import RaiderLib.Drivers.Motors.Motor.MotorType;

public class MotorFactory {
    public static Motor createMotor(MotorType type, int CANID, String canbus, MotorConfiguration config){
      switch(type){
        case FALCON500:
          return new Falcon500(CANID, canbus, config);
        case KRAKENX60:
          return new KrakenX60(CANID, canbus, config);
        default:
          throw new IllegalArgumentException("Illegal Motor Passed to Factory Method, You probably tried to create a REV Motor and specify the CAN Bus");
      }
    }
    public static Motor createMotor(MotorType type, int CANID, MotorConfiguration config){
      switch(type){
        case CANSPARKMAX:
          return new SparkMAX(CANID, config);
        case CANSPARKFLEX:
          return new SparkFlex(CANID, config);
        default:
          throw new IllegalArgumentException("Illegal Motor Passed to Factory Method, You probably tried to make a CTRE Motor without specifying the CAN Bus");
      }
    }
}
