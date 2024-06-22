package RaiderLib.Logging;

import java.io.Writer;
import java.util.concurrent.BlockingQueue;

import RaiderLib.Logging.Logger.LogTable;

public class LoggingThread extends Thread{

    private BlockingQueue<LogTable> updatesQueue;
    private NTLogger ntLogger = new NTLogger();

    public LoggingThread(BlockingQueue<LogTable> updatesQueue) {
        this.setDaemon(true);
        this.updatesQueue = updatesQueue;
    }

    public void run() {
        try {
            while (true) {
                // Await the next update
                LogTable updateTable = updatesQueue.take();

                // Send the update table to each writer somehow
                ntLogger.sendUpdates(updateTable); // Need to change API if adding more writers

            }
        } catch (InterruptedException exception) {
            // Error printed in the main thread
        }
    }

}
