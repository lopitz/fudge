package com.lolplane.fudge.cli;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import lombok.Getter;

@Getter
public class MultipleIOException extends IOException {

    private final List<IOException> causes;

    public MultipleIOException(List<IOException> exceptions) {
        super("Multiple IO exceptions: " + exceptions.stream().filter(Objects::nonNull).map(Exception::getMessage).collect(Collectors.joining("\n")));

        this.causes = exceptions.stream().filter(Objects::nonNull).toList();
    }

}
