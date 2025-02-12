package com.lolplane.fudge;

import java.io.PrintWriter;
import java.io.StringWriter;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Delegate;

@SuppressWarnings("java:S106") //lopitz: explicitly for console output, not logging output
public class ConsoleWriter {

    @Delegate
    private final PrintWriter printWriter;

    @Getter
    @Setter
    private boolean debugEnabled = false;

    public ConsoleWriter() {
        printWriter = new PrintWriter(System.out, true);
    }

    public ConsoleWriter(PrintWriter printWriter) {
        this.printWriter = printWriter;
    }

    public PrintWriter printWriter() {
        return printWriter;
    }

    public void debug(String message, Object... args) {
        if (debugEnabled) {
            println("DEBUG", message, args);
        }
    }

    public void warn(String message, Object... args) {
        println("WARN", message, args);
    }

    public void error(String message, Object... args) {
        println("ERROR", message, args);
    }

    private void println(String level, String message, Object... args) {
        println("%s: %s".formatted(level, logFormat(message, args)));
    }

    public void println(String message, Object... args) {
        println(logFormat(message, args));
    }

    private String logFormat(String message, Object[] args) {
        return replaceParameters(message, args) + createStackTrace(args);
    }

    private String replaceParameters(String message, Object[] args) {
        int currentPosition = message.indexOf("{}");
        int currentArg = 0;
        var result = new StringBuilder();
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

    private String createStackTrace(Object[] args) {
        if (args.length > 0 && args[args.length - 1] instanceof Throwable throwable) {
            var sw = new StringWriter();
            throwable.printStackTrace(new PrintWriter(sw));
            return " " + sw;
        }
        return "";
    }
}
