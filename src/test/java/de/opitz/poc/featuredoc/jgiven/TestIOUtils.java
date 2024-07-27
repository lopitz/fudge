package de.opitz.poc.featuredoc.jgiven;

import java.net.URL;

import lombok.experimental.UtilityClass;

@UtilityClass
public class TestIOUtils {

    public static URL loadFile(String source) {
        return ClassLoader.getSystemClassLoader().getResource(source);
    }

}
