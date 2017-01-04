package io.logmatic.android;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;
import android.util.Log;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Logger {


    public static final String TAG = "logmatic";
    private final String name;
    private final LogmaticAppender appender;
    private ArrayMap<String, JsonElement> extraFields = new ArrayMap<>();
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
     *
     * @param name          Application withName
     * @param appender      The appender
     * @param timestamping  True to enable event timestamping
     * @param legacyLogging True to add legacy (logcat) logging
     * @param extraFields   Global fields added to all events
     */
    public Logger(final @NonNull String name,
                  final @NonNull LogmaticAppender appender,
                  final boolean timestamping,
                  final boolean legacyLogging,
                  final @NonNull ArrayMap<String, JsonElement> extraFields) {

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

    public Logger addField(final @NonNull String key, final @Nullable String value) {
        extraFields.put(key, gson.toJsonTree(value));
        return this;
    }

    public Logger addField(final @NonNull String key, final @Nullable Long value) {
        extraFields.put(key, gson.toJsonTree(value));
        return this;
    }

    public Logger addField(final @NonNull String key, final @Nullable Integer value) {
        extraFields.put(key, gson.toJsonTree(value));
        return this;
    }

    public Logger addField(final @NonNull String key, final @Nullable Float value) {
        extraFields.put(key, gson.toJsonTree(value));
        return this;
    }

    public Logger addField(final @NonNull String key, final @Nullable Double value) {
        extraFields.put(key, gson.toJsonTree(value));
        return this;
    }

    public Logger addField(final @NonNull String key, final @Nullable Boolean value) {
        extraFields.put(key, gson.toJsonTree(value));
        return this;
    }

    public Logger addField(final @NonNull String key, final @Nullable Date value) {
        extraFields.put(key, gson.toJsonTree(value != null ? value.getTime() : null));
        return this;
    }

    public Logger removeField(final @NonNull String key) {
        extraFields.remove(key);
        return this;
    }

    // Private methods
    private void internalLog(int level, String message) {
        internalLog(level, message, null);
    }

    private void internalLog(int level, String message, Object context) {

        // if it's enabled, log with Logcat as usual
        if (legacyLogging) {
            Log.println(level, name, message);
        }


        // instantiate a new Json object from the context
        JsonObject event = new JsonObject();

        try {
            if (context != null) {
                JsonElement root = gson.toJsonTree(context);
                if (root.isJsonObject()) event = root.getAsJsonObject();
            }

        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }

        // add extra and global fields
        final int extraFieldsCount = extraFields.size();
        for (int i = 0; i < extraFieldsCount; i++) {
            event.add(extraFields.keyAt(i), extraFields.valueAt(i));

        }

        // add mandatory fields
        event.addProperty("message", message);
        event.addProperty("severity", getLevelAsString(level));
        event.addProperty("appname", name);


        // add datetime field if it's enabled
        if (timestamping) {
            event.addProperty("date", simpleDateFormat.format(new Date()));
        }

        // send the event to the appender as string
        appender.append(gson.toJson(event));
    }

    /**
     * Simple matcher fro Logcat levels and default syslog levels
     */
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
