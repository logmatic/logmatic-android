package io.logmatic.asynclogger.appender;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import io.logmatic.asynclogger.appender.net.EndpointManager;
import io.logmatic.asynclogger.appender.net.TCPEndpoint;


public class LogmaticAppender implements Appender {


    private final String token;
    private boolean isConnected;

    private EndpointManager manager;


    public static final String DST_HOST = "api.logmatic.io";
    public static final int DST_PORT = 10514;

    private static BlockingDeque<String> cache;

    ScheduledThreadPoolExecutor scheduler = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(1);

    static {
        cache = new LinkedBlockingDeque();
    }



    public LogmaticAppender(String token, EndpointManager manager) {


        if (manager == null) {
            TCPEndpoint endpoint = new TCPEndpoint(DST_HOST, DST_PORT);
            this.manager = new EndpointManager(endpoint, cache);
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
                LogmaticAppender.this.timeToWriteLogs();
            }

        };

        // Start the initial runnable task by posting through the handler
        scheduler.scheduleAtFixedRate(periodicCronTask, 0, 2, TimeUnit.MINUTES);

    }

    public final void stop() {
        manager.shutdown();
        scheduler.shutdown();
    }


    public void append(String data) {
        // prefix all events by token
        cache.offerLast(token + " " + data);
        //cache.offerLast(data);
    }


    @Override
    public void updateNetworkStatus(boolean isConnected) {
        this.isConnected = isConnected;
    }


    /**
     * Write logs
     */
    public void timeToWriteLogs() {

        // do nothing if we don't have a connection
        if (isConnected == false) return;

        // bulk events
        boolean isBulked = false;
        List<String> bulk = new LinkedList();

        while (isBulked) {

            String element = cache.pollLast();
            if (element == null) break;
            bulk.add(element);

        }

        // send them
        manager.write(bulk);


    }
}
