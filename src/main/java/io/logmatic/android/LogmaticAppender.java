package io.logmatic.android;

import android.util.Log;

import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import io.logmatic.android.endpoint.Endpoint;
import io.logmatic.android.endpoint.SecureTCPEndpoint;


public class LogmaticAppender {

    private static final String TAG = "Logmatic";
    private static final long IDLE_TIME_SECONDS = 60;
    /* Customer API token */
    private final String token;

    /* Network state */
    private boolean isConnected = true;

    /* Constants props */
    private static final String DST_HOST = "api.logmatic.io";
    private static final int SSL_DST_PORT = 10515;

    /* Internal manager to handle the Logmatic connection  */
    private EndpointManager manager;

    /* Simple cron daemon */
    ScheduledThreadPoolExecutor scheduler = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(1);

    /* The events queue */
    private ConcurrentLinkedDeque<String> cache = new ConcurrentLinkedDeque<>();


    public LogmaticAppender(String token, EndpointManager manager) {

        // Set the network state
        // FIXME: Find a way to set the default network, without any connect.

        Log.i(getClass().getSimpleName(), "Network state initialization, isConnected: " + isConnected);

        if (manager == null) {
            Endpoint endpoint = new SecureTCPEndpoint(DST_HOST, SSL_DST_PORT);
            this.manager = new EndpointManager(endpoint);
        }

        this.token = token;
        start();

    }


    /**
     * Start the scheduler, and a periodic task
     */
    public final void start() {
        // Periodic callback for sending logs
        Runnable periodicCronTask = new Runnable() {
            @Override
            public void run() {
                LogmaticAppender.this.tick();
            }
        };

        scheduler.scheduleAtFixedRate(periodicCronTask, 0, IDLE_TIME_SECONDS, TimeUnit.SECONDS);

    }


    public final void stop() {
        //FIXME: add a better stop handler
        tick();
        scheduler.shutdownNow();
        manager.shutdown();
    }


    public void append(String data) {
        // prefix all events by token
        cache.offer(token + ' ' + data + '\n');
    }


    /**
     * Try to send logs when a tick occurred
     */
    public void tick() {

        Log.v(TAG, "cron - tick()");

        // do nothing if we don't have a connection or the cache is empty
        if (!isConnected || cache.isEmpty()) return;

        Log.d(TAG, "Start a new async task to send events to Logmation.io");
        manager.doInBackground(cache);

    }


    public void updateNetworkStatus(boolean isConnected) {

        Log.d(TAG, "Network status changed, isConnected: " + isConnected);
        this.isConnected = isConnected;
    }

}
