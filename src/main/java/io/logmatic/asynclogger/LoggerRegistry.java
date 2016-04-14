package io.logmatic.asynclogger;

import java.util.LinkedList;

/**
 * Created by gpolaert on 4/14/16.
 */
public class LoggerRegistry {

    private static LinkedList<Logger> loggers = new LinkedList();

    public static void updateNetworkStatus(boolean isConnected) {


        for (Logger logger : loggers) {

            logger.appender.updateNetworkStatus(isConnected);

        }


    }


    public static void register(Logger l) {
        loggers.add(l);
    }
}
