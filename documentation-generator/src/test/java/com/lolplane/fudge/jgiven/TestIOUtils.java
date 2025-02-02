package com.lolplane.fudge.jgiven;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.stream.Collectors;

import lombok.experimental.UtilityClass;

@UtilityClass
public class TestIOUtils {

    public static URL fileUrl(String source) {
        return ClassLoader.getSystemClassLoader().getResource(source);
    }

    public static String loadTextFile(String source) {
        try (var reader = new BufferedReader(new InputStreamReader(fileUrl(source).openStream()))) {
            return reader.lines().collect(Collectors.joining("\n"));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}
