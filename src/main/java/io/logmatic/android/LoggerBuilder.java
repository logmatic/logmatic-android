package io.logmatic.android;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Utility class for Logger building
 */
public class LoggerBuilder {

    public final static String DEFAULT_LOGGERNAME = "android-log";

    private String token;
    private LogmaticAppender appender;
    private boolean timestamping = true;
    private boolean legacyLogging = true;
    private String name = DEFAULT_LOGGERNAME;
    private Set<Map.Entry<String, JsonElement>> extraFields = new HashSet();


    public LoggerBuilder init(String youLogmaticKey) {
        this.token = youLogmaticKey;
        return this;
    }

    public LoggerBuilder name(String name) {
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

    public void addFields(JsonObject extraFields) {
        this.extraFields = extraFields.entrySet();
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
