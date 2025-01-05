package com.lolplane.fudge.tools;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LineBufferTest {

    @Test
    @DisplayName("should return a stream of lines when buffer contains lines")
    void shouldReturnStreamOfLinesWhenBufferContainsLines() throws Exception {
        try (var lineBuffer = new LineBuffer()) {
            var writer = lineBuffer.printWriter();
            writer.println("Line 1");
            writer.println("Line 2");

            var lines = lineBuffer.lines().toList();

            assertThat(lines).containsExactly("Line 1", "Line 2");
        }
    }

    @Test
    @DisplayName("should return an empty stream when buffer is empty")
    void shouldReturnEmptyStreamWhenBufferIsEmpty() throws Exception {
        try (var lineBuffer = new LineBuffer()) {
            var lines = lineBuffer.lines().toList();

            assertThat(lines).isEmpty();
        }
    }

    @Test
    @DisplayName("should handle multiline buffer correctly")
    void shouldHandleMultilineBufferCorrectly() throws Exception {
        try (var lineBuffer = new LineBuffer()) {
            var writer = lineBuffer.printWriter();
            writer.print("First line\nSecond line\nThird line");

            var lines = lineBuffer.lines().toList();

            assertThat(lines).containsExactly("First line", "Second line", "Third line");
        }
    }
}
