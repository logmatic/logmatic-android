package io.logmatic.android.endpoint;

import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

/**
 * Provides a Secure TCP connection to Logmatic.io
 */
public class SecureTCPEndpoint implements Endpoint {

    /* Constants props */
    public static final String LOGMATIC_DST_HOST = "api.logmatic.io";
    public static final int LOGMATIC_SSL_DST_PORT = 10515;

    private SSLSocket socket;
    private DataOutputStream stream;
    private final String hostname;
    private final Integer port;

    public SecureTCPEndpoint(String hostname, Integer port) {
        this.port = port;
        this.hostname = hostname;
        this.socket = null;

    }


    @Override
    public boolean send(String data) {

        if (!isConnected()) return false;

        try {
            stream.write(data.getBytes());
            return true;

        } catch (IOException e) {
            Log.e(getClass().getName(), "Failed to send data to the endpoint", e);

        }
        return false;
    }

    @Override
    public boolean flush() {
        try {
            stream.flush();
        } catch (IOException e) {
            return false;
        }
        return true;
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
                Socket s = new Socket(hostname, port);
                SSLSocketFactory sslFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
                this.socket = (SSLSocket) sslFactory.createSocket(s, hostname, port, true);
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
