package RaiderLib.Logging;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import edu.wpi.first.hal.HALUtil;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.StringPublisher;

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
            updatesQueue.put(new LogTable(updatesMap, HALUtil.getFPGATime()));
        }
    }

    public static record LogTable(Map<String, LoggedValue> updatesMap, long timestamp) {}
}
