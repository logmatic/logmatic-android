package io.logmatic.asynclogger;

import android.util.Log;

import io.logmatic.asynclogger.net.EndpointManager;


public class LogmaticAppender {


    private EndpointManager manager = null;


    public final void start() {
        if (manager == null) {
            Log.e(getClass().getName(), "No ConnectionManager set for this appender.");
        }

        if (manager != null) {
            manager.start();
        }

    }

    public final void stop() {
        if (manager != null) {
            manager.kill();
        }
    }


    public void append(String data) {

        manager.write(data);
    }


}
