package io.logmatic.asynclogger.endpoint;

import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;


public class TCPEndpoint implements Endpoint {

    private Socket socket;
    private DataOutputStream stream;
    private final String hostname;
    private final Integer port;

    public TCPEndpoint(String hostname, Integer port) {
        this.port = port;
        this.hostname = hostname;
        this.socket = null;

        openConnection();
    }

    @Override
    public boolean isBulkable() {
        return false;
    }

    @Override
    public boolean send(String data) {

        if (!isConnected()) return false;

        try {
            stream.write((data + '\n').getBytes());
            return true;

        } catch (IOException e) {
            Log.e(getClass().getName(), "Failed to send data to the endpoint", e);

        }
        return false;
    }

    @Override
    public boolean isConnected() {
        return socket.isConnected();
    }


    @Override
    public void closeConnection() {

        try {
            stream.flush();
            stream.close();
            socket.close();
        } catch (IOException e) {
            Log.e(getClass().getName(), "Connection shutdown failed", e);
        } catch (NullPointerException ne) {
            Log.v(getClass().getName(), "Connection shutdown failed, no previous connection have been open");
        }

    }

    @Override
    public boolean openConnection() {


        try {

            if (socket == null || socket.isClosed()) {
                socket = new Socket(hostname, port);
            }

            stream = new DataOutputStream(socket.getOutputStream());
            return true;

        } catch (IOException e) {

            e.printStackTrace();
            Log.e(getClass().getName(), "Failed to open socket", e);
        }
        return false;
    }

}
