package io.logmatic.asynclogger.net;

/**
 * Created by gpolaert on 4/14/16.
 */
public interface Endpoint {

    boolean isBulkable();

    boolean send(String data);

    boolean isConnected();

    void closeConnection();

    boolean openConnection();
}
