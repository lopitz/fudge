package de.opitz.fudge;

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

}
