package com.lolplane.fudge.utils;

import java.io.PrintWriter;

import com.lolplane.fudge.ConsoleWriter;
import lombok.RequiredArgsConstructor;
import org.apache.maven.plugin.logging.Log;

@RequiredArgsConstructor
public class LogConsoleWriter implements ConsoleWriter {

    private final Log log;

    public void info(String message, Object... args) {
        if (log.isInfoEnabled()) {
            log.info(logFormat(message, args));
        }
    }

    @Override
    public void debug(String message, Object... args) {
        if (log.isDebugEnabled()) {
            log.debug(logFormat(message, args));
        }
    }

    @Override
    public void warn(String message, Object... args) {
        if (log.isWarnEnabled()) {
            log.warn(logFormat(message, args));
        }
    }

    @Override
    public void error(String message, Object... args) {
        if (log.isErrorEnabled()) {
            log.error(logFormat(message, args));
        }
    }

    @Override
    public PrintWriter printWriter() {
        return new PrintWriter(new LogPrintWriter(log));
    }

}
