package RaiderLib.Logging;

import edu.wpi.first.networktables.GenericPublisher;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.PubSubOption;
import java.util.HashMap;
import java.util.Map;

import RaiderLib.Logging.Logger.LogTable;

public class NTLogger{
    private final NetworkTableInstance nt = NetworkTableInstance.getDefault();

    private final Map<String, GenericPublisher> publishers = new HashMap<>();

    public void sendUpdates(LogTable logTable) {
        for (Map.Entry<String, LoggedValue> field : logTable.updatesMap().entrySet()) {
            // Skip any values with NT logging disabled
            if (field.getValue().sendToNT) {

                // Create publisher if necessary
                String key = field.getKey();
                GenericPublisher publisher = publishers.get(key);
                if (publisher == null) {
                    publisher = nt.getTopic(key)
                            .genericPublish(field.getValue().getNT4Type(), PubSubOption.sendAll(true));
                    publishers.put(key, publisher);
                }

                // Send updates to NetworkTables and DataLog
                switch (field.getValue().type) {
                    case Raw:
                        publisher.setRaw((byte[]) field.getValue().getLogValue(), logTable.timestamp());
                        break;
                    case Boolean:
                        publisher.setBoolean((boolean) field.getValue().getLogValue(), logTable.timestamp());
                        break;
                    case BooleanArray:
                        publisher.setBooleanArray((boolean[]) field.getValue().getLogValue(), logTable.timestamp());
                        break;
                    case Integer:
                        publisher.setInteger((int) field.getValue().getLogValue(), logTable.timestamp());
                        break;
                    case IntegerArray:
                        publisher.setIntegerArray((long[]) field.getValue().getLogValue(), logTable.timestamp());
                        break;
                    case Float:
                        publisher.setFloat((float) field.getValue().getLogValue(), logTable.timestamp());
                        break;
                    case FloatArray:
                        publisher.setFloatArray((float[]) field.getValue().getLogValue(), logTable.timestamp());
                        break;
                    case Double:
                        publisher.setDouble((double) field.getValue().getLogValue(), logTable.timestamp());
                        break;
                    case DoubleArray:
                        publisher.setDoubleArray((double[]) field.getValue().getLogValue(), logTable.timestamp());
                        break;
                    case String:
                        publisher.setString((String) field.getValue().getLogValue(), logTable.timestamp());
                        break;
                    case StringArray:
                        publisher.setStringArray((String[]) field.getValue().getLogValue(), logTable.timestamp());
                        break;
                }
            }
        }
    }
}