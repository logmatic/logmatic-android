package io.logmatic.asynclogger.net;


import com.google.gson.JsonObject;

public class LogmaticClient {


    public static final int DST_PORT = 10515;
    public static final String DST_HOST = "api.logmatic.io";
    StringBuilder eventBuilder = new StringBuilder();
    //public static final String DST_HOST = "192.168.50.45";

    private SSLSocketEndpoint endpoint;
    private String key;
    private JsonObject extraArgs;

    public LogmaticClient(String logmaticAPIKey) {

        new LogmaticClient(key, new SSLSocketEndpoint(DST_HOST, DST_PORT));

    }

    public LogmaticClient(String logmaticAPIKey, SSLSocketEndpoint customEndpoint) {

        key = logmaticAPIKey;
        endpoint = customEndpoint;

        extraArgs = new JsonObject();

    }


    public void log(String message) {

        JsonObject event = extraArgs.getAsJsonObject();
        event.addProperty("message", message);

        String data = eventBuilder
                .append(key)
                .append(" ")
                .append(event.toString()).toString();

        endpoint.send(data.getBytes());

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
}
