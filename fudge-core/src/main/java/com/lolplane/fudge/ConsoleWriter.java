package com.lolplane.fudge;

import java.io.PrintWriter;

import lombok.experimental.Delegate;

@SuppressWarnings("java:S106") //lopitz: explicitly for console output, not logging output
public class ConsoleWriter {

    @Delegate
    private final PrintWriter printWriter;

    public ConsoleWriter() {
        printWriter = new PrintWriter(System.out, true);
    }

    public ConsoleWriter(PrintWriter printWriter) {
        this.printWriter = printWriter;
    }

    public PrintWriter printWriter() {
        return printWriter;
    }

    public void warn(String message, Object... args) {
        println("WARN", message, args);
    }

    public void error(String message, Object... args) {
        println("ERROR", message, args);
    }

    private void println(String level, String message, Object... args) {
        printf("%s: %s", level, logFormat(message, args));
    }

    private String logFormat(String message, Object[] args) {
        int currentPosition = message.indexOf("{}");
        int currentArg = 0;
        StringBuilder result = new StringBuilder();
        int lastPosition = 0;

        while (currentPosition != -1) {
            result.append(message, lastPosition, currentPosition);
            if (currentArg < args.length) {
                result.append(args[currentArg++]);
            } else {
                result.append("{}");
            }
            lastPosition = currentPosition + 2;
            currentPosition = message.indexOf("{}", lastPosition);
        }
        result.append(message.substring(lastPosition));
        return result.toString();
    }
}
