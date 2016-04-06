package io.logmatic.asynclogger.net;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;


public class SSLSocketEndpoint {

    private SSLSocket sslSocket;
    private OutputStream stream;

    public SSLSocketEndpoint(String hostname, int port) {

        try {

            Socket socket = new Socket(hostname, port);
            SSLSocketFactory sslFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            sslSocket = (SSLSocket) sslFactory.createSocket(socket, hostname, port, true);

            // link the writer
            stream = sslSocket.getOutputStream();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public void send(byte[] data) {

        try {

            stream.write(data);
            stream.flush();

        } catch (IOException e) {

            e.printStackTrace();

        }

    }

    public boolean isConnected() {
        return false;
    }
}
