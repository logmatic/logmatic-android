package io.logmatic.asynclogger;

import java.util.LinkedList;
import java.util.List;

/**
 * A collection of all logger references
 */
public class LoggerRegistry {

    private static List<Logger> loggers = new LinkedList();


    /**
     * Broadcast the network status to all loggers
     *
     * @param isConnected True if the network is available
     */
    public static void updateNetworkStatus(boolean isConnected) {
        for (Logger logger : loggers) {
            logger.getAppender().updateNetworkStatus(isConnected);
        }
    }

    /**
     * Store the logger reference
     **/
    public static void register(Logger logger) {
        loggers.add(logger);
    }
}
