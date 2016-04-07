package io.logmatic.asynclogger.net;

import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;


public class SSLSocketEndpoint {

    private SSLSocket sslSocket;
    private OutputStream stream;
    private final String hostname;
    private final Integer port;

    public SSLSocketEndpoint(String hostname, Integer port) {
        this.port = port;
        this.hostname = hostname;
        this.sslSocket = null;

        tryConnnection();
    }

    public SSLSocketEndpoint(SSLSocket socket) {
        this.port = null;
        this.hostname = null;
        this.sslSocket = socket;

        tryConnnection();
    }


    public boolean send(byte[] data) {

        if (!isConnected()) return false;

        try {
            stream.write(data);
            stream.flush();
            return true;

        } catch (IOException e) {
            Log.e(getClass().getName(), "Failed to send data to the endpoint", e);

        }
        return false;
    }

    public boolean isConnected() {
        return sslSocket.isConnected();
    }

    public boolean tryConnnection() {

        if (isConnected()) return true;

        closeConnection();
        return openConnection();

    }

    public void closeConnection() {

        try {
            stream.flush();
            sslSocket.close();
        } catch (IOException e) {
            Log.e(getClass().getName(), "Connection close failed", e);
        } catch (NullPointerException ne) {
            Log.v(getClass().getName(), "Connection close failed, no previous connection have been open");
        }

    }

    protected boolean openConnection() {


        try {

            if (sslSocket == null) {
                Socket socket = new Socket(hostname, port);
                SSLSocketFactory sslFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
                sslSocket = (SSLSocket) sslFactory.createSocket(socket, hostname, port, true);
            }

            stream = sslSocket.getOutputStream();
            return true;

        } catch (IOException e) {
            Log.e(getClass().getName(), "Failed to open socket", e);
        }
        return false;
    }

}
