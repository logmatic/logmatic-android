package io.logmatic.asynclogger.appender;

/**
 * Created by gpolaert on 4/14/16.
 */
public interface Appender {
    void updateNetworkStatus(boolean isConnected);

    void append(String s);

    void stop();
}
