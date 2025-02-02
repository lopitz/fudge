package com.lolplane.fudge.tools;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

public class LineBuffer implements AutoCloseable {

    private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    private final PrintWriter printWriter = new PrintWriter(buffer, true);

    public PrintWriter printWriter() {
        return printWriter;
    }

    public Stream<String> lines() throws IOException {
        printWriter.flush();
        buffer.flush();
        try (var reader = new BufferedReader(new StringReader(buffer.toString(StandardCharsets.UTF_8)))) {
            return reader.lines().toList().stream();
        }
    }

    @Override
    public void close() throws Exception {
        printWriter.close();
        buffer.close();
    }

}
