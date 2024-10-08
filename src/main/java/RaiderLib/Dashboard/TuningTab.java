package RaiderLib.Dashboard;

import RaiderLib.Config.PIDConstants;
import RaiderLib.Drivers.Motors.Motor;
import edu.wpi.first.networktables.DoubleSubscriber;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEvent;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import java.util.EnumSet;
import java.util.function.Supplier;

public class TuningTab {
  static int col = 0;

  static {
    NetworkTableInstance nt = NetworkTableInstance.getDefault();
    NetworkTable table = nt.getTable("Shuffleboard").getSubTable("Tuning");
  }

  public static void tunePIDs(String title, PIDConstants constants, Supplier<Void> apply) {
    ShuffleboardTab tuningTab = Shuffleboard.getTab("Tuning");

    tuningTab.add(title + " P", constants.kP).withSize(2, 1).withPosition(col, 0);
    tuningTab.add(title + " I", constants.kI).withSize(2, 1).withPosition(col + 1, 0);
    tuningTab.add(title + " D", constants.kD).withSize(2, 1).withPosition(col + 2, 0);
    tuningTab.add(title + " F", constants.kF).withSize(2, 1).withPosition(col + 3, 0);

    col += 4;

    NetworkTableInstance inst = NetworkTableInstance.getDefault();
    NetworkTable ntTable = inst.getTable("Shuffleboard").getSubTable("Auto");

    DoubleSubscriber psub = ntTable.getDoubleTopic(title + " P").subscribe(0.0);

    inst.addListener(
        psub,
        EnumSet.of(NetworkTableEvent.Kind.kValueAll),
        event -> {
          constants.kP = event.valueData.value.getDouble();
          apply.get();
        });

    DoubleSubscriber isub = ntTable.getDoubleTopic(title + " I").subscribe(0.0);

    inst.addListener(
        isub,
        EnumSet.of(NetworkTableEvent.Kind.kValueAll),
        event -> {
          // can only get doubles because it's a DoubleSubscriber, but
          // could check value.isDouble() here too
          constants.kI = event.valueData.value.getDouble();
          apply.get();
        });

    DoubleSubscriber dsub = ntTable.getDoubleTopic(title + " D").subscribe(0.0);

    inst.addListener(
        dsub,
        EnumSet.of(NetworkTableEvent.Kind.kValueAll),
        event -> {
          constants.kD = event.valueData.value.getDouble();
          apply.get();
        });

    DoubleSubscriber fsub = ntTable.getDoubleTopic(title + " F").subscribe(0.0);

    inst.addListener(
        fsub,
        EnumSet.of(NetworkTableEvent.Kind.kValueAll),
        event -> {
          constants.kF = event.valueData.value.getDouble();
          apply.get();
        });
  }

  static void tuneMotorPIDs(Motor motor, String title) {
    tunePIDs(
        title + " slot0",
        motor.getConfiguration().PIDConfigs.slot0Configs,
        () -> {
          motor.configMotor();
          return null;
        });
    tunePIDs(
        title + " slot1",
        motor.getConfiguration().PIDConfigs.slot1Configs,
        () -> {
          motor.configMotor();
          return null;
        });
    tunePIDs(
        title + " slot2",
        motor.getConfiguration().PIDConfigs.slot2Configs,
        () -> {
          motor.configMotor();
          return null;
        });
  }
}
