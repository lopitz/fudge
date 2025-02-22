package com.lolplane.fudge;

import java.io.PrintWriter;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Delegate;

@SuppressWarnings("java:S106") //lopitz: explicitly for console output, not logging output
public class PrintWriterConsoleWriter implements ConsoleWriter {

    @Delegate
    private final PrintWriter printWriter;

    @Getter
    @Setter
    private boolean debugEnabled = false;

    public PrintWriterConsoleWriter() {
        printWriter = new PrintWriter(System.out, true);
    }

    public PrintWriterConsoleWriter(PrintWriter printWriter) {
        this.printWriter = printWriter;
    }

    public PrintWriter printWriter() {
        return printWriter;
    }

    @Override
    public void debug(String message, Object... args) {
        if (debugEnabled) {
            println("DEBUG", message, args);
        }
    }

    @Override
    public void info(String message, Object... args) {
        println("INFO", message, args);
    }

    @Override
    public void warn(String message, Object... args) {
        println("WARN", message, args);
    }

    @Override
    public void error(String message, Object... args) {
        println("ERROR", message, args);
    }

    private void println(String level, String message, Object... args) {
        println("%s: %s".formatted(level, logFormat(message, args)));
    }

    public void println(String message, Object... args) {
        println(logFormat(message, args));
    }

}
