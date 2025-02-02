package com.lolplane.fudge;

import java.io.PrintWriter;

import com.lolplane.fudge.tools.LineBuffer;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ConsoleWriterTest {
    private final LineBuffer capturedOutput = new LineBuffer();

    @SneakyThrows
    @Test
    @DisplayName("should replace placeholders with given arguments")
    void shouldReplacePlaceholdersWithGivenArguments() {
        var writer = new ConsoleWriter(new PrintWriter(capturedOutput.printWriter()));

        writer.warn("parameter1={}, parameter2={}, parameter3={}", "one", "two", 3);

        assertThat(capturedOutput.lines()).containsExactly("WARN: parameter1=one, parameter2=two, parameter3=3");
    }

    @SneakyThrows
    @Test
    @DisplayName("should handle missing arguments gracefully and append the placeholder")
    void shouldHandleMissingArgumentsGracefullyAndAppendThePlaceholder() {
        var writer = new ConsoleWriter(capturedOutput.printWriter());

        writer.warn("parameter1={}, parameter2={}, parameter3={}", "one", "two");

        assertThat(capturedOutput.lines()).containsExactly("WARN: parameter1=one, parameter2=two, parameter3={}");
    }

    @SneakyThrows
    @Test
    @DisplayName("should ignore arguments that have no matching placeholder")
    void shouldIgnoreArgumentsThatHaveNoMatchingPlaceholder() {
        var writer = new ConsoleWriter(capturedOutput.printWriter());

        writer.warn("parameter1={}, parameter2={}", "one", "two", 3);

        assertThat(capturedOutput.lines()).containsExactly("WARN: parameter1=one, parameter2=two");
    }

    @SneakyThrows
    @Test
    @DisplayName("should produce debug output when debug is activated")
    void shouldProduceDebugOutputWhenDebugIsActivated() {
        var writer = new ConsoleWriter(capturedOutput.printWriter());
        writer.setDebugEnabled(true);
        writer.debug("debug message");
        assertThat(capturedOutput.lines()).contains("DEBUG: debug message");
    }

    @SneakyThrows
    @Test
    @DisplayName("should not produce debug output when debug is not active")
    void shouldNotProduceDebugOutputWhenDebugIsNotActive() {
        var writer = new ConsoleWriter(capturedOutput.printWriter());
        writer.setDebugEnabled(false);
        writer.debug("debug message not to be shown");
        assertThat(capturedOutput.lines()).isEmpty();
    }
}
