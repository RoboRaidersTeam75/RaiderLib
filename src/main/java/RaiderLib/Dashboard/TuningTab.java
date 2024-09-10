package RaiderLib.Dashboard;

import java.util.EnumSet;

import RaiderLib.Config.PIDConstants;
import edu.wpi.first.networktables.DoubleSubscriber;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEvent;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;

public class TuningTab {
  static int col = 0;

  static {
    NetworkTableInstance nt = NetworkTableInstance.getDefault();
    NetworkTable table = nt.getTable("Shuffleboard").getSubTable("Tuning");    
  }

  public static void tunePIDs(String title, PIDConstants constants) {
    ShuffleboardTab tuningTab = Shuffleboard.getTab("Tuning");

    tuningTab.add(title+" P", constants.kP).withSize(2, 1).withPosition(col, 0);
    tuningTab.add(title+" I", constants.kI).withSize(2, 1).withPosition(col+1, 0);
    tuningTab.add(title+" D", constants.kD).withSize(2, 1).withPosition(col+2, 0);
    tuningTab.add(title+" F", constants.kF).withSize(2, 1).withPosition(col+3, 0);

    col += 4;

    NetworkTableInstance inst = NetworkTableInstance.getDefault();
    NetworkTable ntTable = inst.getTable("Shuffleboard").getSubTable("Auto");

    DoubleSubscriber psub = ntTable.getDoubleTopic(title+" P").subscribe(0.0);

     inst.addListener(
        psub,
        EnumSet.of(NetworkTableEvent.Kind.kValueAll),
        event -> {
          constants.kP = event.valueData.value.getDouble();
        });

    DoubleSubscriber isub = ntTable.getDoubleTopic(title+" I").subscribe(0.0);

     inst.addListener(
        isub,
        EnumSet.of(NetworkTableEvent.Kind.kValueAll),
        event -> {
          // can only get doubles because it's a DoubleSubscriber, but
          // could check value.isDouble() here too
          constants.kI = event.valueData.value.getDouble();
        });

    DoubleSubscriber dsub = ntTable.getDoubleTopic(title+" D").subscribe(0.0);

    inst.addListener(
        dsub,
        EnumSet.of(NetworkTableEvent.Kind.kValueAll),
        event -> {
          constants.kD = event.valueData.value.getDouble();
        });
      
    DoubleSubscriber fsub = ntTable.getDoubleTopic(title+" F").subscribe(0.0);

    inst.addListener(
        fsub,
        EnumSet.of(NetworkTableEvent.Kind.kValueAll),
        event -> {
          constants.kF = event.valueData.value.getDouble();
        });
  }
}
