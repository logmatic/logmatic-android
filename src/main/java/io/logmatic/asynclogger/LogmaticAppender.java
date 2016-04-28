package io.logmatic.asynclogger;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import io.logmatic.asynclogger.endpoint.Endpoint;
import io.logmatic.asynclogger.endpoint.SecureTCPEndpoint;


public class LogmaticAppender {

    private static final int MAX_EVENTS_PER_BULK = 1000;
    private static final long MAX_BYTES_PER_BULK = 1024 * 100; // 100KB
    /* Customer API token */
    private final String token;

    /* Network state */
    private boolean isConnected = true;

    /* Constants props */
    private static final String DST_HOST = "api.logmatic.io";
    private static final int DST_PORT = 10515;

    /* Internal manager to handle the Logmatic connection  */
    private EndpointManager manager;

    /* Simple cron daemon */
    ScheduledThreadPoolExecutor scheduler = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(1);

    /* The events queue */
    private Queue<String> cache = new ConcurrentLinkedQueue<>();


    public LogmaticAppender(String token, EndpointManager manager) {

        if (manager == null) {
            Endpoint endpoint = new SecureTCPEndpoint(DST_HOST, DST_PORT);
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

        // Start the initial runnable task by posting through the handler
        //scheduler.scheduleAtFixedRate(periodicCronTask, 0, 2, TimeUnit.MINUTES);

        scheduler.scheduleAtFixedRate(periodicCronTask, 0, 10, TimeUnit.SECONDS);

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

        // do nothing if we don't have a connection
        if (!isConnected) return;


        int numberOfEventsCached = cache.size();
        int numberOfEventsSent = 0;

        System.out.println("tick");


        while (numberOfEventsSent < numberOfEventsCached) {

            // bulk events
            StringBuilder bulk = new StringBuilder();
            int numberOfEvents = 0;

            while (numberOfEvents < MAX_EVENTS_PER_BULK && bulk.length() < MAX_BYTES_PER_BULK) {
                String element = cache.poll();
                if (element == null) break; // empty queues
                numberOfEvents++;
                bulk.append(element);
            }

            // send them
            if (numberOfEvents != 0) {
                System.out.println(numberOfEvents);
                manager.handle(bulk.toString());
            }

            numberOfEventsSent += numberOfEvents;
            System.out.println(numberOfEventsSent);
        }


    }


    public void updateNetworkStatus(boolean isConnected) {
        this.isConnected = isConnected;
    }
}
