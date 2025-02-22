package com.lolplane.fudge.utils;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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

    @RequiredArgsConstructor
    private static class LogPrintWriter extends Writer {

        private final Log log;
        private final StringBuilder buffer = new StringBuilder();
        private final Lock bufferLock = new ReentrantLock();

        @Override
        public void write(char[] characters, int offset, int length) throws IOException {
            if ((offset < 0) || (offset > characters.length) || (length < 0) ||
                ((offset + length) > characters.length) || ((offset + length) < 0)) {
                throw new IndexOutOfBoundsException();
            } else if (length == 0) {
                return;
            }
            append(new String(characters, offset, length));
        }

        @Override
        public void write(String message) throws IOException {
            append(message);
        }

        @Override
        public void write(String message, int offset, int length) throws IOException {
            append(message.substring(offset, offset + length));
        }

        @Override
        public Writer append(CharSequence message) throws IOException {
            boolean containsNewLine = String.valueOf(message).contains("\n");
            executeLocked(() -> buffer.append(message));
            if (containsNewLine) {
                flush();
            }
            return this;
        }

        @Override
        public void flush() {
            log.info(buffer.toString());
            executeLocked(() -> this.buffer.delete(0, buffer.length()));
        }

        private void executeLocked(Runnable runnable) {
            bufferLock.lock();
            try {
                runnable.run();
            } finally {
                bufferLock.unlock();
            }
        }

        @Override
        public void close() {
            flush();
        }
    }
}
