package io.logmatic.asynclogger;

import io.logmatic.asynclogger.appender.Appender;

/**
 * Utility class for Logger building
 */
public class LoggerBuilder {


    private String token;
    private Appender appender;
    private boolean timestamping = true;
    private boolean legacyLogging = true;


    public LoggerBuilder init(String youLogmaticKey) {
        this.token = youLogmaticKey;
        return this;
    }

    public LoggerBuilder setCustomAppender(Appender appender) {
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



    public Logger build() {
        Logger l = new Logger(token, appender, timestamping, legacyLogging);
        LoggerRegistry.register(l);
        return l;

    }


}
