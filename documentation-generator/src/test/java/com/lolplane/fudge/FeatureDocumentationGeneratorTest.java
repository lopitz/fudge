package com.lolplane.fudge;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FeatureDocumentationGeneratorTest {

    private static PrintStream originalOut = System.out;

    private final ByteArrayOutputStream capturedOutput = new ByteArrayOutputStream();

    @BeforeAll
    static void beforeAllOnce() {
        originalOut = System.out;
    }

    @BeforeEach
    void beforeAll() {
        System.setOut(new PrintStream(capturedOutput));
    }

    @AfterAll
    static void afterAllOnce() {
        System.setOut(originalOut);
    }

    @Test
    @DisplayName("should forward call from main method")
    void shouldForwardCallFromMainMethod() {
        FeatureDocumentationGenerator.main(new String[]{"--invalid-arg"});
        assertThat(capturedOutput.toString(StandardCharsets.UTF_8)).contains("Parsing failed. Reason: Unrecognized option: --invalid-arg");
    }

    @Test
    @DisplayName("should print help message if no arguments are given")
    void shouldPrintHelpMessageIfNoArgumentsAreGiven() {
        FeatureDocumentationGenerator.main(new String[]{});
        assertThat(capturedOutput.toString(StandardCharsets.UTF_8)).contains("usage: FeatureDocumentationGenerator");
    }

    @Test
    @DisplayName("should call document generator if help was not requested")
    void shouldCallDocumentGeneratorIfHelpWasNotRequested() {
        var expected = Path.of("/non-existing path:!");
        FeatureDocumentationGenerator.main(new String[]{"-n", "-s", "/non-existing path:!", "-t", "FeatureDocumentationGeneratorTest"});
        assertThat(capturedOutput.toString(StandardCharsets.UTF_8)).contains("The given source directory [%s] does not exist.".formatted(expected.toString()));
    }

    @SneakyThrows
    @Test
    @DisplayName("should call document generator with valid args given")
    void shouldCallDocumentGeneratorWithValidArgsGiven() {
        FeatureDocumentationGenerator.main(new String[]{"-n", "-s", "src/test/resources", "-v", "-t", "FeatureDocumentationGeneratorTest"});
        capturedOutput.flush();
        assertThat(capturedOutput.toString(StandardCharsets.UTF_8)).contains(
            "DEBUG: Created folder base_award_calculation",
            "DEBUG: Created folder profile",
            "DEBUG: Created folder yearly_limit"
        );
    }
}
