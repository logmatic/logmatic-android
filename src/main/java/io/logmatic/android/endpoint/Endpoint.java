package io.logmatic.android.endpoint;

/**
 * Endpoints are the way to connect to Logmatic
 */
public interface Endpoint {

    boolean send(String data);

    boolean flush();

    boolean isConnected();

    void closeConnection();

    boolean openConnection();
}
