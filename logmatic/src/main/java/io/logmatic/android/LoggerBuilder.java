package io.logmatic.android;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

import java.util.Date;

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
    private ArrayMap<String, JsonElement> extraFields = new ArrayMap<>();

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


    public LoggerBuilder addField(final @NonNull String key, final @Nullable String value) {
        extraFields.put(key, gson.toJsonTree(value));
        return this;
    }

    public LoggerBuilder addField(final @NonNull String key, final @Nullable Long value) {
        extraFields.put(key, gson.toJsonTree(value));
        return this;
    }

    public LoggerBuilder addField(final @NonNull String key, final @Nullable Integer value) {
        extraFields.put(key, gson.toJsonTree(value));
        return this;
    }

    public LoggerBuilder addField(final @NonNull String key, final @Nullable Float value) {
        extraFields.put(key, gson.toJsonTree(value));
        return this;
    }

    public LoggerBuilder addField(final @NonNull String key, final @Nullable Double value) {
        extraFields.put(key, gson.toJsonTree(value));
        return this;
    }

    public LoggerBuilder addField(final @NonNull String key, final @Nullable Boolean value) {
        extraFields.put(key, gson.toJsonTree(value));
        return this;
    }

    public LoggerBuilder addField(final @NonNull String key, final @Nullable Date value) {
        extraFields.put(key, gson.toJsonTree(value != null ? value.getTime() : null));
        return this;
    }

    public Logger build() {
        if (appender == null) {
            // Create a new appender, use generic options
            appender = new LogmaticAppender(token, null);
        }

        final Logger l = new Logger(name, appender, timestamping, legacyLogging, extraFields);
        LoggerRegistry.register(name, l);
        return l;
    }

}
