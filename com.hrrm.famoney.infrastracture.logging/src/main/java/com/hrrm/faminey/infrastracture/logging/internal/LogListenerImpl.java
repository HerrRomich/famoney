package com.hrrm.faminey.infrastracture.logging.internal;

import java.text.MessageFormat;
import java.util.Date;

import org.osgi.service.log.LogEntry;
import org.osgi.service.log.LogListener;

public class LogListenerImpl implements LogListener {

    @Override
    public void logged(LogEntry entry) {
        String message = MessageFormat.format("{2} {1} [{0}] {3}, {4}", entry.getThreadInfo(), entry.getLogLevel(),
                                              new Date(entry.getTime()), entry.getLoggerName(), entry.getMessage());
        System.out.println(message);
        if (entry.getException() != null) {
            entry.getException()
                .printStackTrace();
        }
    }

}
