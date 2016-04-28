package io.logmatic.asynclogger;

import android.util.Log;

import io.logmatic.asynclogger.endpoint.Endpoint;


public class EndpointManager {

    private static final long RECONNECTION_WAIT = 500; // 500 ms
    private static final int MAX_ATTEMPT = 2;


    private Endpoint endpoint;


    public EndpointManager(Endpoint endpoint) {
        this.endpoint = endpoint;
    }

    public void run(String payload) {


        // while the network `networkStatus` is available
        // bulk all messages and send it to the endpoint
        // if it's failed, retry until MAX_ATTEMPT

        try {

            int attempt = 0;


            while (attempt <= MAX_ATTEMPT) {


                // Open a connection to the endpoint
                endpoint.openConnection();

                if (endpoint.isConnected()) {


                    boolean isSent = endpoint.send(payload);
                    if (isSent) {
                        break;

                    }

                    attempt++;
                    endpoint.closeConnection();
                    Thread.sleep(RECONNECTION_WAIT);


                }
            }


        } catch (Exception e) {
            e.printStackTrace();
            Log.wtf(getClass().getName(), e);
        } finally {
            endpoint.closeConnection();
        }

    }


    public void shutdown() {

        endpoint.closeConnection();

    }

    public void handle(String payload) {

        this.run(payload);


    }
}
