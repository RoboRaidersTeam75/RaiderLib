package RaiderLib.Logging;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import edu.wpi.first.hal.HALUtil;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.StringPublisher;
import edu.wpi.first.wpilibj.DriverStation;

public class Logger {
    private static int queueCapacity = 50 * 4; // ~ 4 seconds

    private static Map<String, LoggedValue> updatesMap = new HashMap<>();
    private static BlockingQueue<LogTable> updatesQueue = new ArrayBlockingQueue<>(queueCapacity);

    private static LoggingThread loggingThread = new LoggingThread(updatesQueue);

    static {
        loggingThread.start();
    }

    private static StringPublisher messagePublisher = NetworkTableInstance.getDefault()
        .getTable("Messages")
        .getStringTopic("messages")
        .publish();

    public static void message(String message) { 
        messagePublisher.set(message);
    }

    public static void updateQueue() {
        try {
            updatesQueue.add(new LogTable(updatesMap, HALUtil.getFPGATime()));
            updatesMap = new HashMap<>();
        } catch (IllegalStateException exception) {
            DriverStation.reportError("Logging queue capacity exceeded, data is no longer being logged.", false);
        }
    }

    public static record LogTable(Map<String, LoggedValue> updatesMap, long timestamp) {}

    private static void writeUpdate(String key, LoggedValue value) {
        updatesMap.put(key, value);
    }

    public static void log(String key, boolean value) {
        writeUpdate(key, new LoggedValue(value));
      }
  
      public static void log(String key, boolean[] value) {
        writeUpdate(key, new LoggedValue(value));
      }
  
      public static void log(String key, double value) {
        writeUpdate(key, new LoggedValue(value));
      }
  
      public static void log(String key, double[] value) {
          writeUpdate(key, new LoggedValue(value));
      }
  
      public static void log(String key, long value) {
        writeUpdate(key, new LoggedValue(value));
      }
  
      public static void log(String key, long[] value) {
        writeUpdate(key, new LoggedValue(value));
      }
  
      public static void log(String key, String value) {
        writeUpdate(key, new LoggedValue(value));
      }
  
      public static void log(String key, String[] value) {
        writeUpdate(key, new LoggedValue(value));
      }
}
