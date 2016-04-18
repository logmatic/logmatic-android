package io.logmatic.asynclogger.appender;


import io.logmatic.asynclogger.appender.net.EndpointManager;

public class LogmaticWritingTask implements Runnable {

    private LogmaticAppender logmatic;

    public LogmaticWritingTask(LogmaticAppender logmatic) {
        this.logmatic = logmatic;
    }

    @Override
    public void run() {

        logmatic.timeToSend();

    }
}
