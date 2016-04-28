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


    private final static String TAG_NAME = Logger.class.getSimpleName();
    private final LogmaticAppender appender;
    private JsonObject extraFields = new JsonObject();
    private boolean timestamping = true;
    private boolean legacyLogging = true;


    public Logger(LogmaticAppender appender, boolean timestamping, boolean legacyLogging) {

        this.appender = appender;
        this.timestamping = timestamping;
        this.legacyLogging = legacyLogging;
    }


    public void v(String message) {
        internalLog(Log.VERBOSE, TAG_NAME, message);
    }

    public void v(String tag, String message) {
        internalLog(Log.VERBOSE, tag, message);
    }

    public void v(String tag, String message, Throwable tr) {
        internalLog(Log.VERBOSE, tag, message, tr);
    }

    public void d(String message) {
        internalLog(Log.DEBUG, TAG_NAME, message);
    }

    public void d(String tag, String message) {
        internalLog(Log.DEBUG, tag, message);
    }

    public void d(String tag, String message, Throwable tr) {
        internalLog(Log.DEBUG, tag, message, tr);
    }

    public void i(String message) {
        internalLog(Log.INFO, TAG_NAME, message);
    }

    public void i(String tag, String message) {
        internalLog(Log.INFO, tag, message);
    }

    public void i(String tag, String message, Throwable tr) {
        internalLog(Log.INFO, tag, message, tr);
    }

    public void e(String message) {
        internalLog(Log.ERROR, TAG_NAME, message);
    }

    public void e(String tag, String message) {
        internalLog(Log.ERROR, tag, message);
    }

    public void e(String tag, String message, Throwable tr) {
        internalLog(Log.ERROR, tag, message, tr);
    }

    public void w(String message) {
        internalLog(Log.DEBUG, TAG_NAME, message);
    }

    public void w(String tag, String message) {
        internalLog(Log.DEBUG, tag, message);
    }

    public void w(String tag, String message, Throwable tr) {
        internalLog(Log.DEBUG, tag, message, tr);
    }


    private void internalLog(int level, String tag, String message, Throwable tr) {
        internalLog(level, tag, message + '\n' + Log.getStackTraceString(tr));

    }


    private void internalLog(int level, String tag, String message) {

        if (legacyLogging) {
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



    /* formaters and tools */
    private static final String ISO_8601 = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    private final static SimpleDateFormat simpleDateFormat = new SimpleDateFormat(ISO_8601, Locale.US);
    private static final Gson gson = new GsonBuilder()
            .serializeNulls()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create();




    public LogmaticAppender getAppender() {
        return appender;
    }
}
