package io.logmatic.asynclogger.appender.net;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;

import javax.net.ssl.SSLSocket;


public class HttpsEndpoint implements Endpoint {

    private final String url;
    private HttpURLConnection urlConnection;
    private DataOutputStream stream;

    public HttpsEndpoint(String url) {
        this.url = url;
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

            stream.write(("[" + data + "]").getBytes());

            stream.flush();
            stream.close();

            return true;

        } catch (IOException e) {
            Log.e(getClass().getName(), "Failed to send data to the endpoint", e);

        }
        return false;
    }

    @Override
    public boolean isConnected() {
        //FIXME better way for testing connection
        try {
            urlConnection.setDoOutput(true);
        }catch (IllegalStateException e) {
            if (e.getMessage().equals("Already connected")) {
                return true;
            }
        }
        return false;
    }


    @Override
    public void closeConnection() {

        try {
            stream.flush();
            stream.close();
            urlConnection.disconnect();
        } catch (IOException e) {
            Log.e(getClass().getName(), "Connection shutdown failed", e);
        } catch (NullPointerException ne) {
            Log.v(getClass().getName(), "Connection shutdown failed, no previous connection have been open");
        }

    }

    @Override
    public boolean openConnection() {


        try {
            urlConnection = null;
            urlConnection = (HttpURLConnection) new URL(url).openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("charset", "utf-8");

            stream = new DataOutputStream(urlConnection.getOutputStream());
            return true;

        } catch (Exception e) {

            e.printStackTrace();
            Log.e(getClass().getName(), "Failed to open connection", e);
        }
        return false;

    }
}