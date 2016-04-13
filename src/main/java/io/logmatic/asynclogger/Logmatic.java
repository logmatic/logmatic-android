package io.logmatic.asynclogger;


import android.util.Log;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import io.logmatic.asynclogger.net.EndpointManager;
import io.logmatic.asynclogger.net.SSLSocketEndpoint;

public class Logmatic {


    private final String name = null;
    private final EndpointManager appender = new EndpointManager();


    private static final int DST_PORT = 10515;
    private static final String DST_HOST = "api.logmatic.io";
    private static final String ISO_8601 = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    private static final Gson gson = new GsonBuilder()
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

    public Logmatic(String logmaticAPIKey) {

        new Logmatic(key, new SSLSocketEndpoint(DST_HOST, DST_PORT));

    }

    public Logmatic(String logmaticAPIKey, SSLSocketEndpoint customEndpoint) {

        key = logmaticAPIKey;
        endpoint = customEndpoint;

        extraArgs = new JsonObject();

        simpleDateFormat = new SimpleDateFormat(ISO_8601, Locale.US);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

    }




    public void v(String message) {
        Log.v(getClass().getName(), message);
        log(message);
    }

    public void v(String tag, String message) {
        Log.v(tag, message);
        log(message);
    }

    public void v(String tag, String message, Throwable tr) {
        Log.v(tag, message, tr);
        log(message);
    }

    public void d(String message) {
        Log.d(getClass().getName(), message);
        log(message);
    }

    public void d(String tag, String message) {
        Log.d(tag, message);
        log(message);
    }

    public void d(String tag, String message, Throwable tr) {
        Log.d(tag, message, tr);
        log(message);
    }

    public void i(String message) {
        Log.i(getClass().getName(), message);
        log(message);
    }

    public void i(String tag, String message) {
        Log.i(tag, message);
        log(message);
    }

    public void i(String tag, String message, Throwable tr) {
        Log.i(tag, message, tr);
        log(message);
    }

    public void w(String message) {
        Log.w(getClass().getName(), message);
        log(message);
    }

    public void w(String tag, String message) {
        Log.w(tag, message);
        log(message);
    }

    public void w(String tag, String message, Throwable tr) {
        Log.w(tag, message, tr);
        log(message);
    }

    public void e(String message) {
        Log.e(getClass().getName(), message);
        log(message);
    }

    public void e(String tag, String message) {
        Log.e(tag, message);
        log(message);
    }

    public void e(String tag, String message, Throwable tr) {
        Log.e(tag, message, tr);
        log(message);
    }

    public void wtf(String message) {
        Log.wtf(getClass().getName(), message);
        log(message);
    }

    public void wtf(String tag, String message) {
        Log.wtf(tag, message);
        log(message);
    }

    public void wtf(String tag, String message, Throwable tr) {
        Log.wtf(tag, message, tr);
        log(message);
    }









    public static JsonElement toJson(Object source) {
        return gson.toJsonTree(source).;
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











    /* add metadata of all types */

    public void addMeta(String key, String value) {
        extraArgs.addProperty(key, value);
    }

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
