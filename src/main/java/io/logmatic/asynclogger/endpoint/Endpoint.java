package io.logmatic.asynclogger.endpoint;

/**
 * Endpoints are a type of connections to Logmatic
 */
public interface Endpoint {


    boolean isBulkable();

    boolean send(String data);

    boolean isConnected();

    void closeConnection();

    boolean openConnection();
}
