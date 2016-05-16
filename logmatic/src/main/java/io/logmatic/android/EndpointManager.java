package io.logmatic.android;

import android.os.AsyncTask;
import android.util.Log;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import io.logmatic.android.endpoint.Endpoint;


public class EndpointManager extends AsyncTask<Deque<String>, Integer, Void> {

    private static final long RECONNECTION_WAIT = 300; // between two attempts
    private static final int MAX_ATTEMPT = 3;
    private static final int MAX_EVENTS_PER_BULK = 1000;
    private static final int MAX_BYTES_PER_BULK = 1024 * 100; // 100 KB


    /** The endpoint implementation */
    private Endpoint endpoint;


    public EndpointManager(Endpoint endpoint) {
        super();
        this.endpoint = endpoint;
    }


    public void shutdown() {
        endpoint.closeConnection();
    }


    @Override
    protected Void doInBackground(Deque<String>... sources) {


        // actually, we have only one source.
        for (Deque<String> source : sources) {

            try {

                int attempt = 0;
                while (attempt <= MAX_ATTEMPT) {

                    // Prepare the bulks
                    // Sending all events already store in the source
                    // if new events come, they will be handled to next call (@see LogmaticAppender.tick())
                    int minOfEventsToSend = source.size();
                    int numberOfEventsSent = 0;
                    boolean withoutFailure = true;

                    Log.v(Logger.TAG, "new attempt, sending " + minOfEventsToSend + " events at minimum");
                    Log.v(Logger.TAG, "opening a new connection to Logmatic.io");


                    // start a new connection
                    if (endpoint.openConnection()) {


                        int j = 0;
                        // loop into the events, and bulk them
                        while (numberOfEventsSent < minOfEventsToSend && withoutFailure) {

                            // bulk events
                            List<String> bulk = new LinkedList<>();
                            int totalOfBytes = 0;

                            // Check the max requirement
                            while (bulk.size() < MAX_EVENTS_PER_BULK && totalOfBytes < MAX_BYTES_PER_BULK) {
                                String element = source.pollFirst();
                                if (element == null) break; // empty source;
                                totalOfBytes += element.length();
                                bulk.add(element);
                            }

                            // inline bulk
                            StringBuilder payload = new StringBuilder();
                            for (String element : bulk) payload.append(element);

                            // send events
                            withoutFailure = endpoint.send(payload.toString());
                            endpoint.flush();

                            // handle a failure
                            if (!withoutFailure) {

                                // readd the events to the source
                                for (int i = bulk.size() - 1; i >= 0; i--) {
                                    source.offerFirst(bulk.get(i));
                                    bulk.remove(i);
                                }

                                Log.v(Logger.TAG, "Bulk[ " + j + " ] failed,  rollback!");
                                break;

                            }


                            numberOfEventsSent += bulk.size();
                            Log.v(Logger.TAG, "Bulk[ " + j + " ] sent,  events: " + bulk.size() + "/" + numberOfEventsSent);
                            j++;
                        }
                        if (withoutFailure) break;
                    }


                    Log.v(Logger.TAG, "attempt " + attempt + " failed, waiting for a new connection");

                    attempt++;
                    endpoint.closeConnection();
                    Thread.sleep(RECONNECTION_WAIT);

                }


            } catch (Exception e) {
                Log.e(getClass().getName(), e.getMessage(), e);
            } finally {
                endpoint.closeConnection();
            }
        }

        Log.v(Logger.TAG, "Sending loop finished");
        return null;
    }

}
