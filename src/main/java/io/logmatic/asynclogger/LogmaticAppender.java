package io.logmatic.asynclogger;

import android.util.Log;

import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import io.logmatic.asynclogger.endpoint.Endpoint;
import io.logmatic.asynclogger.endpoint.SecureTCPEndpoint;
import io.logmatic.asynclogger.endpoint.TCPEndpoint;


public class LogmaticAppender {

    private static final String TAG = "Logmatic";
    private static final long FREQUENCY = 30;
    /* Customer API token */
    private final String token;

    /* Network state */
    private boolean isConnected = true;

    /* Constants props */
    private static final String DST_HOST = "api.logmatic.io";
    private static final int DST_PORT = 10514;
    private static final int SSL_DST_PORT = 10515;

    /* Internal manager to handle the Logmatic connection  */
    private EndpointManager manager;

    /* Simple cron daemon */
    ScheduledThreadPoolExecutor scheduler = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(1);

    /* The events queue */
    private ConcurrentLinkedDeque<String> cache = new ConcurrentLinkedDeque<>();


    public LogmaticAppender(String token, EndpointManager manager) {

        if (manager == null) {
            //
            // Endpoint endpoint1 = new SecureTCPEndpoint(DST_HOST, SSL_DST_PORT);
            Endpoint endpoint2 = new TCPEndpoint(DST_HOST, DST_PORT);
            this.manager = new EndpointManager(endpoint2);
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

        // Start the initial runnable task by posting through the handler
        //scheduler.scheduleAtFixedRate(periodicCronTask, 0, 2, TimeUnit.MINUTES);

        scheduler.scheduleAtFixedRate(periodicCronTask, 0, FREQUENCY, TimeUnit.SECONDS);

    }


    public final void stop() {
        //todo add a better stop handler
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


        Log.v(TAG, "New tick event");
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
