package io.logmatic.android;

import androidx.annotation.NonNull;
import androidx.collection.ArrayMap;
import android.util.Log;

import java.util.Map;

/**
 * A collection of all logger references
 */
public class LoggerRegistry {

    private static ArrayMap<String, Logger> loggers = new ArrayMap<>();


    /**
     * Broadcast the network status to all loggers
     *
     * @param isConnected True if the network is available
     */
    public static void updateNetworkStatus(boolean isConnected) {
        for (Map.Entry<String, Logger> entry : loggers.entrySet()) {
            Log.i(LoggerRegistry.class.getSimpleName(), "Notify network event to '" + entry.getKey() + " logger");
            entry.getValue().getAppender().updateNetworkStatus(isConnected);
        }
    }

    /**
     * Store the logger reference
     **/
    public static void register(final @NonNull String name,
                                final @NonNull Logger logger) {
        loggers.put(name, logger);
    }

    /**
     * Get a already instantiated logger
     *
     * @param name the logger name
     * @return the desired logger
     */
    public static Logger getLogger(final @NonNull String name) {
        return loggers.get(name);
    }

    /**
     * Get the default instantiated logger
     *
     * @return the default logger
     */
    public static Logger getDefaultLogger() {
        return getLogger(LoggerBuilder.DEFAULT_LOGGERNAME);
    }
}
