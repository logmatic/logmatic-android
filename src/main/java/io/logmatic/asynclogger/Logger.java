package io.logmatic.asynclogger;


import android.util.Log;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Logger {


    /* formaters and tools */
    private static final String ISO_8601 = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    private final static SimpleDateFormat simpleDateFormat = new SimpleDateFormat(ISO_8601, Locale.US);
    private static final Gson gson = new GsonBuilder()
            .serializeNulls()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create();


    private final String name = null;
    private final String token;
    final Appender appender;

    private JsonObject extraFields = new JsonObject();
    private boolean timestamping = true;
    private boolean legacyLogger = true;
    private boolean deviceIdentification = true;


    public Logger(String yourLogmaticKey) {

        this.token = yourLogmaticKey;
        this.appender = new LogmaticAppender(token);

    }

    public Logger(String yourLogmaticKey, Appender appender) {

        this.token = yourLogmaticKey;
        this.appender = appender;


    }


    public void v(String message) {
        log(Log.VERBOSE, getClass().getName(), message);
    }

    public void v(String tag, String message) {
        log(Log.VERBOSE, tag, message);
    }

    public void v(String tag, String message, Throwable tr) {
        log(Log.VERBOSE, tag, message, tr);
    }

    public void d(String message) {
        log(Log.DEBUG, getClass().getName(), message);
    }

    public void d(String tag, String message) {
        log(Log.DEBUG, tag, message);
    }

    public void d(String tag, String message, Throwable tr) {
        log(Log.DEBUG, tag, message, tr);
    }

    public void i(String message) {
        log(Log.INFO, getClass().getName(), message);
    }

    public void i(String tag, String message) {
        log(Log.INFO, tag, message);
    }

    public void i(String tag, String message, Throwable tr) {
        log(Log.INFO, tag, message, tr);
    }

    public void e(String message) {
        log(Log.ERROR, getClass().getName(), message);
    }

    public void e(String tag, String message) {
        log(Log.ERROR, tag, message);
    }

    public void e(String tag, String message, Throwable tr) {
        log(Log.ERROR, tag, message, tr);
    }

    public void w(String message) {
        log(Log.DEBUG, getClass().getName(), message);
    }

    public void w(String tag, String message) {
        log(Log.DEBUG, tag, message);
    }

    public void w(String tag, String message, Throwable tr) {
        log(Log.DEBUG, tag, message, tr);
    }

    public void wtf(String message) {
        if (legacyLogger) {
            Log.wtf(getClass().getName(), message);
        }
        log(Log.ERROR, getClass().getName(), message);
    }

    public void wtf(String tag, String message) {
        if (legacyLogger) {
            Log.wtf(tag, message);
        }
        log(Log.ERROR, tag, message);
    }

    public void wtf(String tag, String message, Throwable tr) {
        if (legacyLogger) {
            Log.wtf(tag, message, tr);
        }
        log(Log.ERROR, tag, message, tr);
    }


    private void log(int level, String tag, String message, Throwable tr) {
        log(level, tag, message + '\n' + Log.getStackTraceString(tr));

    }


    private void log(int level, String tag, String message) {

        if (legacyLogger) {
            Log.println(level, tag, message);
        }

        // compile extra fields
        JsonObject event = extraFields.getAsJsonObject();
        event.addProperty("message", message);


        // add datetime field
        if (timestamping) {
            event.addProperty("datetime", simpleDateFormat.format(new Date()));
        }

        appender.append(gson.toJson(event));


    }


    /**
     * Disable  auto-timestamping of events
     */
    public void disableTimestamping() {
        timestamping = false;
    }

    /**
     * Disable legacy logging (i.e. Log.cat)
     */
    public void disableLegacyLogger() {
        legacyLogger = false;
    }



    /* add metadata of all types */

    public void addField(String key, String value) {
        extraFields.addProperty(key, value);
    }

    public void addField(String key, Long value) {
        extraFields.addProperty(key, value);
    }

    public void addField(String key, Integer value) {
        extraFields.addProperty(key, value);
    }

    public void addField(String key, Float value) {
        extraFields.addProperty(key, value);
    }

    public void addField(String key, Double value) {
        extraFields.addProperty(key, value);
    }

    public void addField(String key, Boolean value) {
        extraFields.addProperty(key, value);
    }

    public void addField(String key, Date value) {
        extraFields.addProperty(key, value.getTime());
    }

    public void destroy() {
        appender.stop();
    }
}
