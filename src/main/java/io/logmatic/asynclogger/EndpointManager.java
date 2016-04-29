package io.logmatic.asynclogger;

import android.os.AsyncTask;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.TimeUnit;

import io.logmatic.asynclogger.endpoint.Endpoint;


public class EndpointManager extends AsyncTask<ConcurrentLinkedDeque<String>, Integer, Void> {

    private static final long RECONNECTION_WAIT = 500; // 500 ms
    private static final int MAX_ATTEMPT = 2;
    private static final int MAX_EVENTS_PER_BULK = 10;
    private static final int MAX_BYTES_PER_BULK = 1024 * 100; // 100 KB
    private static final String LOGGING_TAG = "Logmatic";


    private Endpoint endpoint;


    public EndpointManager(Endpoint endpoint) {
        super();

        this.endpoint = endpoint;
    }


    public void shutdown() {

        endpoint.closeConnection();

    }


    @Override
    protected Void doInBackground(ConcurrentLinkedDeque<String>... sources) {


        // while the network `networkStatus` is available
        // bulk all messages and send it to the endpoint
        // if it's failed, retry until MAX_ATTEMPT

        for (ConcurrentLinkedDeque<String> source : sources) {

            try {

                int attempt = 0;
                while (attempt <= MAX_ATTEMPT) {


                    Log.d(LOGGING_TAG, "Open a new connection to Logmatic.io");



                    if ( endpoint.openConnection()) {

                        // prepare chunks
                        int minOfEventsToSend = source.size(); // freeze
                        int numberOfEventsSent = 0;

                        Log.v(LOGGING_TAG, "Try to send " + minOfEventsToSend + " events (minimum)");

                        int j = 0;
                        while (numberOfEventsSent < minOfEventsToSend) {

                            // bulk events
                            List<String> bulk = new LinkedList<>();
                            int totalOfBytes = 0;

                            // Stack x event into a bulk operation
                            while (bulk.size() < MAX_EVENTS_PER_BULK && totalOfBytes < MAX_BYTES_PER_BULK) {
                                String element = source.pollFirst();
                                if (element == null) break; // empty queues;
                                totalOfBytes += element.length();
                                bulk.add(element);
                            }

                            // inline bulk
                            StringBuilder payload = new StringBuilder();
                            for (String element : bulk) payload.append(element);

                            // send events
                            boolean withoutFailure = endpoint.send(payload.toString());

                            // handle a failure
                            if (!withoutFailure) {

                                for (int i = bulk.size() - 1; i >= 0; i--) {

                                    source.offerFirst(bulk.get(i));
                                    bulk.remove(i);
                                }

                            }


                            numberOfEventsSent += bulk.size();
                            Log.v(LOGGING_TAG, "Bulk(" + j + "] is OK,  " + bulk.size() + "/" + numberOfEventsSent + "events");
                            j++;
                        }

                        break;
                    }


                    Log.d(LOGGING_TAG, "Attempt " + attempt + " failed, waiting for a new connection");

                    attempt++;
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
        return null;
    }

}
