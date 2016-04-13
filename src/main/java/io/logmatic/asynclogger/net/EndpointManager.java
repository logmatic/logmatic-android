package io.logmatic.asynclogger.net;

import android.util.Log;

import java.io.IOException;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.TimeUnit;

/**
 * Created by gpolaert on 4/12/16.
 */
public class EndpointManager extends Thread {


    private static final long MAX_INACTIVITY = 30;
    private static final int MAX_CAPACITY = 10;
    private static final long RECONNECTION_WAIT = 500; // 500 ms
    private static final int MAX_ATTEMPT = -1;
    private static final long PERIODIC_QUEUE_CHECK = 1000;
    private SSLSocketEndpoint endpoint = new SSLSocketEndpoint("api",122);

    private BlockingDeque<String> queue;


    public void start() {


        int attempt = 0;

        // connection loop
        while (isAlive() && attempt <= MAX_ATTEMPT) {


            endpoint.openConnection();

            // sending loop
            while (endpoint.isConnected() && isAlive()) {


                //read
                try {

                    // read the eldest event
                    item = queue.pollFirst(MAX_INACTIVITY, TimeUnit.SECONDS);


                    boolean isSent = endpoint.send(item.getBytes());
                    if (!isSent) {

                        // Trying to rollback, if the deque is full, remove it because
                        // it's the eldest element
                        try {
                            queue.addFirst(item);
                        } catch (IllegalStateException e) {

                            Log.e(getClass().getName(), "Queue is full, removing the eldest element: " + item);

                        }

                        // try to reconnect again
                        break;

                    }

                } catch (InterruptedException e) {

                    Log.i(getClass().getName(), "Inactivity, closing connections");
                    endpoint.closeConnection();
                    while (queue.peekFirst() == null) {

                        Log.i(getClass().getName(), "Queue empty, waiting " + (PERIODIC_QUEUE_CHECK / 1000) + "s before the next check ...");
                        Thread.sleep(PERIODIC_QUEUE_CHECK);

                    }
                }


            }

            endpoint.closeConnection();
            Thread.sleep(RECONNECTION_WAIT);

        }
    }


    public void kill() {

    }
}
