package io.logmatic.asynclogger.appender.net;

import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;


public class SecureTCPEndpoint implements Endpoint {

    private SSLSocket sslSocket;
    private DataOutputStream stream;
    private final String hostname;
    private final Integer port;

    public SecureTCPEndpoint(String hostname, Integer port) {
        this.port = port;
        this.hostname = hostname;
        this.sslSocket = null;

        openConnection();
    }

    public SecureTCPEndpoint(SSLSocket socket) {
        this.port = null;
        this.hostname = null;
        this.sslSocket = socket;

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
        return sslSocket.isConnected();
    }


    @Override
    public void closeConnection() {

        try {
            stream.flush();
            stream.close();
            sslSocket.close();
        } catch (IOException e) {
            Log.e(getClass().getName(), "Connection shutdown failed", e);
        } catch (NullPointerException ne) {
            Log.v(getClass().getName(), "Connection shutdown failed, no previous connection have been open");
        }

    }

    @Override
    public boolean openConnection() {


        try {

            if (sslSocket == null) {
                Socket socket = new Socket(hostname, port);
                SSLSocketFactory sslFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
                sslSocket = (SSLSocket) sslFactory.createSocket(socket, hostname, port, true);
            }

            stream = new DataOutputStream(sslSocket.getOutputStream());
            return true;

        } catch (IOException e) {

            e.printStackTrace();
            Log.e(getClass().getName(), "Failed to open socket", e);
        }
        return false;
    }

}
