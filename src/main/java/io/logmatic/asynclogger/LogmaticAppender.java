package io.logmatic.asynclogger;

import android.util.Log;

import java.util.concurrent.BlockingDeque;

import io.logmatic.asynclogger.net.EndpointManager;
import io.logmatic.asynclogger.net.SSLSocketEndpoint;


public class LogmaticAppender {


    private final String token;
    private EndpointManager manager;
    public static final String DST_HOST = "api.logmatic.io";
    public static final int DST_PORT = 10515;

    public LogmaticAppender(String token) {

        this.token = token;
        // initialize a socket to Logmatic
        SSLSocketEndpoint endpoint = new SSLSocketEndpoint(DST_HOST, DST_PORT);
        manager = new EndpointManager(endpoint);
        start();


    }


    public LogmaticAppender(String token, EndpointManager manager) {

        this.token = token;
        this.manager = manager;


    }


    public final void start() {
        if (manager == null) {
            Log.e(getClass().getName(), "No ConnectionManager set for this appender.");
        }

        if (manager != null) {
            Thread thread = new Thread(manager);
            manager.setCurrentThread(thread);
            thread.start();

        }

    }

    public final void stop() {
        if (manager != null) {
            manager.kill();
        }
    }


    public void append(String data) {

        // prefix all events by token
        manager.write(token + " " + data);

    }


}
