package io.logmatic.asynclogger.net;


import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class LogmaticClient {


    private static final int DST_PORT = 10515;
    private static final String DST_HOST = "api.logmatic.io";
    private static final String ISO_8601 = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    private final Gson gson = new GsonBuilder()
            .serializeNulls()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create();
    ;

    SimpleDateFormat simpleDateFormat = null;
    StringBuilder eventBuilder = new StringBuilder();

    private SSLSocketEndpoint endpoint;
    private String key;
    private JsonObject extraArgs;
    private boolean timestamping = true;

    public LogmaticClient(String logmaticAPIKey) {

        new LogmaticClient(key, new SSLSocketEndpoint(DST_HOST, DST_PORT));

    }

    public LogmaticClient(String logmaticAPIKey, SSLSocketEndpoint customEndpoint) {

        key = logmaticAPIKey;
        endpoint = customEndpoint;

        extraArgs = new JsonObject();

        simpleDateFormat = new SimpleDateFormat(ISO_8601, Locale.US);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

    }


    public void log(String message) {

        JsonObject event = extraArgs.getAsJsonObject();
        event.addProperty("message", message);
        emit(event);

    }
    public void log(Object anonymousObject) {

        JsonObject event = extraArgs.getAsJsonObject();
        event.add("message", gson.toJsonTree(anonymousObject));
        emit(event);
    }

    public String getAPIKey() {
        return key;
    }

    public boolean isConnected() {
        return endpoint.isConnected();
    }

    public void addMeta(String key, String value) {
        extraArgs.addProperty(key, value);
    }

    /* add metadata of all types */

    public void addMeta(String key, Long value) {
        extraArgs.addProperty(key, value);
    }

    public void addMeta(String key, Integer value) {
        extraArgs.addProperty(key, value);
    }

    public void addMeta(String key, Float value) {
        extraArgs.addProperty(key, value);
    }

    public void addMeta(String key, Double value) {
        extraArgs.addProperty(key, value);
    }

    public void addMeta(String key, Boolean value) {
        extraArgs.addProperty(key, value);
    }

    public void addMeta(String key, Date value) {
        extraArgs.addProperty(key, value.getTime());
    }

    public void disableTimestamping() {
        timestamping = false;
    }



    private void emit(JsonObject event) {

        // datetime
        if (timestamping) {
            event.addProperty("datetime", simpleDateFormat.format(new Date()));
        }

        String data = eventBuilder
                .append(key)
                .append(" ")
                .append(event.toString()).toString();

        endpoint.send(data.getBytes());
    }

}
