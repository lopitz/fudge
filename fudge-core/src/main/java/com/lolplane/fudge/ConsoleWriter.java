package com.lolplane.fudge;

import java.io.PrintWriter;
import java.io.StringWriter;

public interface ConsoleWriter {
    void debug(String message, Object... args);

    void info(String message, Object... args);

    void warn(String message, Object... args);

    void error(String message, Object... args);

    default String logFormat(String message, Object[] args) {
        return replaceParameters(message, args) + createStackTrace(args);
    }

    default String replaceParameters(String message, Object[] args) {
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

    default String createStackTrace(Object[] args) {
        if (args.length > 0 && args[args.length - 1] instanceof Throwable throwable) {
            var sw = new StringWriter();
            throwable.printStackTrace(new PrintWriter(sw));
            return " " + sw;
        }
        return "";
    }

    PrintWriter printWriter();
}
