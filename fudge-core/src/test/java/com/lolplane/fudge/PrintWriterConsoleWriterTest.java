package com.lolplane.fudge;

import java.io.PrintWriter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class PrintWriterConsoleWriterTest {

    private static final String MESSAGE = "message";
    private static final String MESSAGE_WITH_FORMAT = MESSAGE + " {}";

    @Mock
    private PrintWriter writer;

    @InjectMocks
    private PrintWriterConsoleWriter consoleWriter;

    @Test
    @DisplayName("should write debug messages if enabled")
    void shouldWriteDebugMessagesIfEnabled() {
        consoleWriter.setDebugEnabled(true);
        consoleWriter.debug(MESSAGE);
        verify(writer).println("DEBUG: " + MESSAGE);
    }

    @Test
    @DisplayName("should not write debug messages if disabled")
    void shouldNotWriteDebugMessagesIfDisabled() {
        consoleWriter.setDebugEnabled(false);
        consoleWriter.debug(MESSAGE);
        verifyNoInteractions(writer);
    }

    @Test
    @DisplayName("should write info messages")
    void shouldWriteInfoMessages() {
        consoleWriter.info(MESSAGE);
        verify(writer).println("INFO: " + MESSAGE);
    }

    @Test
    @DisplayName("should write warn messages")
    void shouldWriteWarnMessages() {
        consoleWriter.warn(MESSAGE);
        verify(writer).println("WARN: " + MESSAGE);
    }

    @Test
    @DisplayName("should write error messages")
    void shouldWriteErrorMessages() {
        consoleWriter.error(MESSAGE);
        verify(writer).println("ERROR: " + MESSAGE);
    }

    @Test
    @DisplayName("should write messages directly")
    void shouldWriteMessagesDirectly() {
        consoleWriter.println(MESSAGE_WITH_FORMAT, "argument");
        verify(writer).println(MESSAGE + " argument");
    }

    @Test
    @DisplayName("should return injected PrintWriter")
    void shouldReturnInjectedPrintWriter() {
        assertThat(consoleWriter.printWriter()).isSameAs(writer);
    }
}
