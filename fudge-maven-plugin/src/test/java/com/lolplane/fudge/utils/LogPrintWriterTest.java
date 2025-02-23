package com.lolplane.fudge.utils;

import lombok.SneakyThrows;
import org.apache.maven.plugin.logging.Log;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LogPrintWriterTest {

    private static final String HELLO_WORLD = " hello world! ";

    @Mock
    private Log log;
    @InjectMocks
    private LogPrintWriter writer;

    @SneakyThrows
    @Test
    @DisplayName("should publish appended messages as info log message")
    void shouldPublishAppendedMessagesAsInfoLogMessage() {
        writer.append("hello");
        writer.flush();
        verify(log).info("hello");
    }

    @SneakyThrows
    @Test
    @DisplayName("should publish written messages as info log message")
    void shouldPublishWrittenMessagesAsInfoLogMessage() {
        writer.write("hello");
        writer.flush();
        verify(log).info("hello");
    }

    @SneakyThrows
    @Test
    @DisplayName("should cache multiple messages until flush is called")
    void shouldCacheMultipleMessagesUntilFlushIsCalled() {
        writer.append("hello");
        writer.append(" world!");
        writer.flush();
        verify(log).info("hello world!");
    }

    @SneakyThrows
    @Test
    @DisplayName("should create an info log message for each part ending with new line and the remainder after flush")
    void shouldCreateAnInfoLogMessageForEachPartEndingWithNewLineAndTheRemainderAfterFlush() {
        writer.append("first\nsecond\nthird");
        writer.flush();
        verify(log).info("first");
        verify(log).info("second");
        verify(log).info("third");
    }

    @Nested
    class PartialMessages {

        @Nested
        class GivenAsString {

            @SneakyThrows
            @Test
            @DisplayName("should create an info message for a selected part")
            void shouldCreateAnInfoMessageForASelectedPart() {
                var message = HELLO_WORLD;
                writer.write(message, 1, message.length() - 2);
                writer.flush();
                verify(log).info("hello world!");
            }

            @SneakyThrows
            @Test
            @DisplayName("should do nothing if source for partial is empty")
            void shouldDoNothingIfSourceForPartialIsEmpty() {
                var message = "";
                writer.write(message.toCharArray(), 0, 0);
                writer.flush();
                verify(log, never()).info(anyString());
            }

            @Test
            @DisplayName("should throw OutOfBoundsException if offset is smaller than 0")
            void shouldThrowOutOfBoundsExceptionIfOffsetIsSmallerThan0() {
                var message = HELLO_WORLD;
                var offset = -1;
                var length = message.length() - 2;
                assertThatCode(() -> writer.write(message, offset, length)).isInstanceOf(IndexOutOfBoundsException.class);
            }

            @Test
            @DisplayName("should throw OutOfBoundsException if offset is greater than input length")
            void shouldThrowOutOfBoundsExceptionIfOffsetIsGreaterThanInputLength() {
                var message = HELLO_WORLD;
                var offset = message.length() + 1;
                var length = 1;
                assertThatCode(() -> writer.write(message, offset, length)).isInstanceOf(IndexOutOfBoundsException.class);
            }

            @Test
            @DisplayName("should throw OutOfBoundsException if length is smaller than 0")
            void shouldThrowOutOfBoundsExceptionIfLengthIsSmallerThan0() {
                var offset = 0;
                var length = -1;
                assertThatCode(() -> writer.write(HELLO_WORLD, offset, length)).isInstanceOf(IndexOutOfBoundsException.class);
            }

            @Test
            @DisplayName("should throw OutOfBoundsException if offset + length is greater than input length")
            void shouldThrowOutOfBoundsExceptionIfOffsetPlusLengthIsGreaterThanInputLength() {
                var message = HELLO_WORLD;
                var offset = message.length() - 1;
                var length = 2;
                assertThatCode(() -> writer.write(message, offset, length)).isInstanceOf(IndexOutOfBoundsException.class);
            }
        }

        @Nested
        class GivenAsCharacterSequence {

            @SneakyThrows
            @Test
            @DisplayName("should create an info message for a selected part of a character sequence")
            void shouldCreateAnInfoMessageForASelectedPartOfACharacterSequence() {
                var message = HELLO_WORLD.toCharArray();
                int length = message.length - 2;
                writer.write(message, 1, length);
                writer.flush();
                verify(log).info("hello world!");
            }

            @Test
            @DisplayName("should throw OutOfBoundsException if offset is smaller than 0")
            void shouldThrowOutOfBoundsExceptionIfOffsetIsSmallerThan0() {
                var message = HELLO_WORLD.toCharArray();
                var offset = -1;
                int length = message.length - 2;
                assertThatCode(() -> writer.write(message, offset, length)).isInstanceOf(IndexOutOfBoundsException.class);
            }

            @Test
            @DisplayName("should throw OutOfBoundsException if offset is greater than input length")
            void shouldThrowOutOfBoundsExceptionIfOffsetIsGreaterThanInputLength() {
                var message = HELLO_WORLD.toCharArray();
                var offset = message.length + 1;
                var length = 1;
                assertThatCode(() -> writer.write(message, offset, length)).isInstanceOf(IndexOutOfBoundsException.class);
            }

            @Test
            @DisplayName("should throw OutOfBoundsException if length is smaller than 0")
            void shouldThrowOutOfBoundsExceptionIfLengthIsSmallerThan0() {
                var message = HELLO_WORLD.toCharArray();
                var offset = 0;
                var length = -1;
                assertThatCode(() -> writer.write(message, offset, length)).isInstanceOf(IndexOutOfBoundsException.class);
            }

            @Test
            @DisplayName("should throw OutOfBoundsException if offset + length is greater than input length")
            void shouldThrowOutOfBoundsExceptionIfOffsetPlusLengthIsGreaterThanInputLength() {
                var message = HELLO_WORLD.toCharArray();
                var offset = message.length - 1;
                assertThatCode(() -> writer.write(message, offset, 2)).isInstanceOf(IndexOutOfBoundsException.class);
            }
        }
    }

    @SneakyThrows
    @Test
    @DisplayName("should flush on close")
    void shouldFlushOnClose() {
        writer.write("flushing");
        verify(log, never()).info("flushing");
        writer.close();
        verify(log).info("flushing");
    }
}
