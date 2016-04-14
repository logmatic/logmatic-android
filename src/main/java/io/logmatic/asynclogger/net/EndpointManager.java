package io.logmatic.asynclogger.net;

import android.util.Log;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;


public class EndpointManager implements Runnable {


    private static final long MAX_INACTIVITY = 30;
    private static final int MAX_CAPACITY = Integer.MAX_VALUE;
    private static final long RECONNECTION_WAIT = 500; // 500 ms
    private static final int MAX_ATTEMPT = 10;
    private static final long PERIODIC_QUEUE_CHECK = 1000;

    private static BlockingDeque<String> queue;
    private Endpoint endpoint;
    private Thread currentThread;
    private boolean networkStatus;


    public EndpointManager(SSLSocketEndpoint endpoint) {
        this.endpoint = endpoint;
        queue = new LinkedBlockingDeque(MAX_CAPACITY);
    }


    @Override
    public void run() {

        try {
            int attempt = 0;
            String item;

            // connection loop
            while (attempt <= MAX_ATTEMPT) {

                endpoint.openConnection();

                // sending loop
                while (endpoint.isConnected()) {


                    //read
                    try {

                        // read the eldest event
                        item = queue.pollFirst(MAX_INACTIVITY, TimeUnit.SECONDS);



                        boolean isSent = endpoint.send(item);
                        if (!isSent) {

                            // Trying to rollback, if the deque is full, remove it because
                            // it's the eldest element
                            try {
                                queue.addFirst(item);
                            } catch (IllegalStateException e) {

                                e.printStackTrace();
                                Log.e(getClass().getName(), "Queue is full, removing the eldest element: " + item);

                            }

                            // try to reconnect again
                            break;

                        }

                    } catch (InterruptedException e) {


                        e.printStackTrace();
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

        } catch (Exception e) {
            e.printStackTrace();
            Log.wtf(getClass().getName(), e);
        } finally {
            endpoint.closeConnection();
        }
    }


    public void kill() {

        while (queue.size() != 0) {
            try {
                System.out.println("queue size:" + queue.size());
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        currentThread.interrupt();

    }

    public void write(String data) {

        queue.offerLast(data);
    }

    public void setCurrentThread(Thread currentThread) {
        this.currentThread = currentThread;
    }

    public void setNetworkStatus(boolean networkStatus) {
        this.networkStatus = networkStatus;
    }
}
