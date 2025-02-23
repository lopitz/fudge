package com.lolplane.fudge.utils;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import lombok.RequiredArgsConstructor;
import org.apache.maven.plugin.logging.Log;

@RequiredArgsConstructor
class LogPrintWriter extends Writer {

    private final Log log;
    private final StringBuilder buffer = new StringBuilder();
    private final Lock bufferLock = new ReentrantLock();

    @Override
    public void write(char[] characters, int offset, int length) throws IOException {
        if ((offset < 0) || (offset > characters.length) || (length < 0) || ((offset + length) > characters.length)) {
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
        executeLocked(() -> {
            if (buffer.isEmpty()) {
                return;
            }
            Arrays
                .stream(buffer.toString().split("\n"))
                .forEach(log::info);
            this.buffer.delete(0, buffer.length());
        });
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
