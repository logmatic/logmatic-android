package io.logmatic.android;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.AbstractMap;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Utility class for Logger building
 */
public class LoggerBuilder {

    public final static String DEFAULT_LOGGERNAME = "android";

    private String token;
    private LogmaticAppender appender;
    private boolean timestamping = true;
    private boolean legacyLogging = true;
    private String name = DEFAULT_LOGGERNAME;
    private Set<Map.Entry<String, JsonElement>> extraFields = new HashSet();

    private static final Gson gson = new GsonBuilder()
            .serializeNulls()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create();


    public LoggerBuilder init(String youLogmaticKey) {
        this.token = youLogmaticKey;
        return this;
    }

    public LoggerBuilder withName(String name) {
        this.name = name;
        return this;
    }


    public LoggerBuilder setCustomAppender(LogmaticAppender appender) {
        this.appender = appender;
        return this;
    }

    public LoggerBuilder disableTimestamping() {
        this.timestamping = false;
        return this;
    }

    public LoggerBuilder disableLegacyLogging() {
        this.legacyLogging = false;
        return this;
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

    public Logger build() {

        if (appender == null) {
            // Create a new appender, use generic options
            appender = new LogmaticAppender(token, null);
        }

        Logger l = new Logger(name, appender, timestamping, legacyLogging, extraFields);
        LoggerRegistry.register(name, l);
        return l;

    }


}
