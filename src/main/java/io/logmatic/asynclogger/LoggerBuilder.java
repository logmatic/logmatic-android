package io.logmatic.asynclogger;

/**
 * Created by gpolaert on 4/14/16.
 */
public class LoggerBuilder {


    private String token;
    private Appender appender;

    public LoggerBuilder init(String youLogmaticKey) {
        this.token = youLogmaticKey;
        return this;
    }

    public LoggerBuilder withAppender(String youLogmaticKey) {
        this.token = youLogmaticKey;
        return this;
    }


    public Logger build() {

        Logger l = (appender == null) ? new Logger(token) : new Logger(token, appender);
        LoggerRegistry.register(l);
        return l;

    }


}
