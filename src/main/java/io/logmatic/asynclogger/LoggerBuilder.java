package io.logmatic.asynclogger;

/**
 * Utility class for Logger building
 */
public class LoggerBuilder {


    private String token;
    private LogmaticAppender appender;
    private boolean timestamping = true;
    private boolean legacyLogging = true;


    public LoggerBuilder init(String youLogmaticKey) {
        this.token = youLogmaticKey;
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



    public Logger build() {

        if(appender == null) {
            // Create a new appender, use generic options
            appender = new LogmaticAppender(token, null);
        }

        Logger l = new Logger(appender, timestamping, legacyLogging);
        LoggerRegistry.register(l);
        return l;

    }


}
