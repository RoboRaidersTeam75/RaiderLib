package RaiderLib.Logging;

import RaiderLib.Logging.LoggedValue.LoggableType;
import RaiderLib.Logging.Logger.LogTable;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.networktables.GenericPublisher;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.PubSubOption;
import edu.wpi.first.networktables.Publisher;
import edu.wpi.first.networktables.StructArrayPublisher;
import java.util.HashMap;
import java.util.Map;

public class NTLogger {
  private final NetworkTableInstance nt = NetworkTableInstance.getDefault();

  private final Map<String, Publisher> publishers = new HashMap<>();

  public void sendUpdates(LogTable logTable) {
    for (Map.Entry<String, LoggedValue> field : logTable.updatesMap().entrySet()) {
      // Skip any values with NT logging disabled
      if (field.getValue().sendToNT) {

        String key = field.getKey();

        if (field.getValue().type == LoggableType.Pose2d) {
          StructArrayPublisher<Pose2d> publisher =
              (StructArrayPublisher<Pose2d>) publishers.get(key);
          if (publisher == null) {
            publisher = nt.getStructArrayTopic(key, Pose2d.struct).publish();
          }
          publishers.put(key, publisher);
          publisher.set((Pose2d[]) field.getValue().getLogValue());

          return;
        } else if (field.getValue().type == LoggableType.SwerveModulePosition) {
          StructArrayPublisher<SwerveModulePosition> publisher =
              (StructArrayPublisher<SwerveModulePosition>) publishers.get(key);
          if (publisher == null) {
            publisher = nt.getStructArrayTopic(key, SwerveModulePosition.struct).publish();
          }
          publishers.put(key, publisher);
          publisher.set((SwerveModulePosition[]) field.getValue().getLogValue());
        } else if (field.getValue().type == LoggableType.SwerveModuleState) {
          StructArrayPublisher<SwerveModuleState> publisher =
              (StructArrayPublisher<SwerveModuleState>) publishers.get(key);
          if (publisher == null) {
            publisher = nt.getStructArrayTopic(key, SwerveModuleState.struct).publish();
          }
          publishers.put(key, publisher);
          publisher.set((SwerveModuleState[]) field.getValue().getLogValue());
        }

        // Create publisher if necessary
        GenericPublisher publisher = (GenericPublisher) publishers.get(key);
        if (publisher == null) {
          publisher =
              nt.getTopic(key)
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
            publisher.setBooleanArray(
                (boolean[]) field.getValue().getLogValue(), logTable.timestamp());
            break;
          case Integer:
            publisher.setInteger((int) field.getValue().getLogValue(), logTable.timestamp());
            break;
          case IntegerArray:
            publisher.setIntegerArray(
                (long[]) field.getValue().getLogValue(), logTable.timestamp());
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
            publisher.setDoubleArray(
                (double[]) field.getValue().getLogValue(), logTable.timestamp());
            break;
          case String:
            publisher.setString((String) field.getValue().getLogValue(), logTable.timestamp());
            break;
          case StringArray:
            publisher.setStringArray(
                (String[]) field.getValue().getLogValue(), logTable.timestamp());
            break;
          default:
            break;
        }
      }
    }
  }
}
