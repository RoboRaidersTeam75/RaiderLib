package RaiderLib.Drivers.Motors;

import RaiderLib.Config.MotorConfiguration;
import RaiderLib.Drivers.Motors.Motor.MotorType;

public class MotorFactory {
    public static Motor createMotor(MotorType type, MotorConfiguration config){
      switch(type){
        case FALCON500:
          return new Falcon500(config);
        case KRAKENX60:
          return new KrakenX60(config);
        case CANSPARKMAX:
          return new SparkMAX(config);
        case CANSPARKFLEX:
          return new SparkFlex(config);
        default:
          throw new IllegalArgumentException("Illegal Motor Passed to Factory Method.");
      }
    }

}
