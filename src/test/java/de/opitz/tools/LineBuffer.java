package de.opitz.tools;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import de.opitz.poc.featuredoc.cli.MultipleIOException;

public class LineBuffer implements AutoCloseable {

    private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    private final PrintWriter printWriter = new PrintWriter(buffer, true);
    private final List<Reader> usedReaders = new LinkedList<>();

    public PrintWriter printWriter() {
        return printWriter;
    }

    public Stream<String> lines() throws IOException {
        printWriter.flush();
        buffer.flush();
        var reader = new BufferedReader(new StringReader(buffer.toString(StandardCharsets.UTF_8)));
        usedReaders.add(reader);
        return reader.lines();
    }

    @Override
    public void close() throws Exception {
        var exceptions = usedReaders.stream().map(this::closeReader).filter(Objects::nonNull).toList();

        if (exceptions.isEmpty()) {
            return;
        }
        throw new MultipleIOException(exceptions);
    }

    private IOException closeReader(Reader reader) {
        try {
            reader.close();
        } catch (IOException e) {
            return e;
        }
        return null;
    }
}
