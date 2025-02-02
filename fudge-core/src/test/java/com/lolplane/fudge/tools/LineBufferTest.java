package com.lolplane.fudge.tools;

import java.io.IOException;
import java.io.Reader;

import com.lolplane.fudge.exceptions.MultipleIOException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.InstanceOfAssertFactories.LIST;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

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

    @Test
    @DisplayName("should throw MultiIoException on multiple exceptions occurring")
    void shouldThrowMultiIoExceptionOnMultipleExceptionsOccurring() {
        var failingReader1 = mock(Reader.class);
        var failingReader2 = mock(Reader.class);

        try {
            doThrow(new IOException("Reader 1 failed")).when(failingReader1).close();
            doThrow(new IOException("Reader 2 failed")).when(failingReader2).close();
        } catch (IOException e) {
            // Ignored in test due to mocking
        }

        var lineBuffer = new LineBuffer();

        // Add mocked readers to LineBuffer
        lineBuffer.usedReaders.add(failingReader1);
        lineBuffer.usedReaders.add(failingReader2);

        // Act and Assert
        assertThatThrownBy(lineBuffer::close)
            .isInstanceOf(MultipleIOException.class)
            .extracting(ex -> ((MultipleIOException) ex).getCauses())
            .asInstanceOf(LIST)
            .hasSize(2)
            .extracting(e -> ((Throwable) e).getMessage())
            .containsExactlyInAnyOrder("Reader 1 failed", "Reader 2 failed");

    }
}
