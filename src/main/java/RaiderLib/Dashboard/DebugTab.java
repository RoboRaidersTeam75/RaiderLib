package RaiderLib.Dashboard;

import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;

public class DebugTab {
    static int currentCol = 0;
    static int currentRow = 0;

    static boolean getOrAddDebugToggle(String names) {
        ShuffleboardTab tuningTab = Shuffleboard.getTab("Debug");

        return tuningTab
            .add(names, false)
            .withWidget(BuiltInWidgets.kToggleSwitch)
            .withSize(2, 1)
            .withPosition(0, 2).getEntry().getBoolean(false);
    }
}
