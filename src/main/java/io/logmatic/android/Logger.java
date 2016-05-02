package io.logmatic.android;


import android.util.Log;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.text.SimpleDateFormat;
import java.util.AbstractMap;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class Logger {


    private final String name;
    private final LogmaticAppender appender;
    private Set<Map.Entry<String, JsonElement>> extraFields;
    private boolean timestamping;
    private boolean legacyLogging;

    /* formatter and tools */
    private static final String ISO_8601 = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    private final static SimpleDateFormat simpleDateFormat = new SimpleDateFormat(ISO_8601, Locale.US);
    private static final Gson gson = new GsonBuilder()
            .serializeNulls()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create();


    /**
     * Default constructor
     *  @param name          Application name
     * @param appender      The appender
     * @param timestamping  True to enable event timestamping
     * @param legacyLogging True to add legacy (logcat) logging
     * @param extraFields   Global fields added to all events
     */
    public Logger(String name, LogmaticAppender appender, boolean timestamping, boolean legacyLogging, Set<Map.Entry<String, JsonElement>> extraFields) {

        this.name = name;
        this.appender = appender;
        this.timestamping = timestamping;
        this.legacyLogging = legacyLogging;
        this.extraFields = extraFields;
    }


    public void v(String message) {
        internalLog(Log.VERBOSE, message);
    }

    public void v(String message, Object context) {
        internalLog(Log.VERBOSE, message, context);
    }

    public void d(String message) {
        internalLog(Log.DEBUG, message);
    }

    public void d(String message, Object context) {
        internalLog(Log.DEBUG, message, context);
    }

    public void i(String message) {
        internalLog(Log.INFO, message);
    }

    public void i(String message, Object context) {
        internalLog(Log.INFO, message, context);
    }

    public void w(String message) {
        internalLog(Log.DEBUG, message);
    }

    public void w(String message, Object context) {
        internalLog(Log.DEBUG, message, context);
    }

    public void e(String message) {
        internalLog(Log.ERROR, message);
    }

    public void e(String message, Object context) {
        internalLog(Log.ERROR, message, context);
    }

    public void wtf(String message) {
        internalLog(Log.ERROR, message);
    }

    public void wtf(String message, Object context) {
        internalLog(Log.ERROR, message, context);
    }


    public void addField(String key, String value) {
        extraFields.add(new AbstractMap.SimpleEntry(key, gson.toJsonTree(value)));
    }

    public void addField(String key, Long value) {
        extraFields.add(new AbstractMap.SimpleEntry(key, gson.toJsonTree(value)));
        ;
    }

    public void addField(String key, Integer value) {
        extraFields.add(new AbstractMap.SimpleEntry(key, gson.toJsonTree(value)));
        ;
    }

    public void addField(String key, Float value) {
        extraFields.add(new AbstractMap.SimpleEntry(key, gson.toJsonTree(value)));
        ;
    }

    public void addField(String key, Double value) {
        extraFields.add(new AbstractMap.SimpleEntry(key, gson.toJsonTree(value)));
        ;
    }

    public void addField(String key, Boolean value) {
        extraFields.add(new AbstractMap.SimpleEntry(key, gson.toJsonTree(value)));
        ;
    }

    public void addField(String key, Date value) {

        extraFields.add(new AbstractMap.SimpleEntry(key, gson.toJsonTree(value.getTime())));
    }


    // Private methods


    private void internalLog(int level, String message) {
        internalLog(level, message, null);
    }


    private void internalLog(int level, String message, Object context) {

        if (legacyLogging) {
            Log.println(level, name, message);
        }

        // compile extra fields
        JsonObject event = new JsonObject();
        for (Map.Entry<String, JsonElement> e : extraFields) {
            event.add(e.getKey(), e.getValue());
        }
        event.addProperty("message", message);
        event.addProperty("level", getLevelAsString(level));
        event.addProperty("appname", name);

        // add context
        if (context != null) event.add("context", gson.toJsonTree(context));

        // add datetime field
        if (timestamping) {
            event.addProperty("datetime", simpleDateFormat.format(new Date()));
        }

        appender.append(gson.toJson(event));


    }

    private String getLevelAsString(int level) {

        switch (level) {
            case Log.ERROR:
                return "ERROR";
            case Log.WARN:
                return "WARN";
            case Log.INFO:
                return "INFO";
            case Log.DEBUG:
                return "DEBUG";
            case Log.VERBOSE:
                return "TRACE";
            default:
                return "DEBUG";
        }
    }


    public LogmaticAppender getAppender() {
        return appender;
    }
}
