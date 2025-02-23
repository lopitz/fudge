package com.lolplane.fudge.utils;

import org.apache.maven.plugin.logging.Log;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LogConsoleWriterTest {

    public static final String MESSAGE = "message";
    @Mock
    private Log log;
    @InjectMocks
    private LogConsoleWriter writer;

    @Test
    @DisplayName("should log to info if info is enabled")
    void shouldLogToInfoIfInfoIsEnabled() {
        when(log.isInfoEnabled()).thenReturn(true);
        writer.info(MESSAGE);
        verify(log).info(MESSAGE);
    }

    @Test
    @DisplayName("should not log to info if info is disabled")
    void shouldNotLogToInfoIfInfoIsDisabled() {
        when(log.isInfoEnabled()).thenReturn(false);
        writer.info(MESSAGE);
        verify(log, never()).info(MESSAGE);
    }

    @Test
    @DisplayName("should log to warn if warn is enabled")
    void shouldLogToWarnIfInfoIsEnabled() {
        when(log.isWarnEnabled()).thenReturn(true);
        writer.warn(MESSAGE);
        verify(log).warn(MESSAGE);
    }

    @Test
    @DisplayName("should not log to warn if warn is disabled")
    void shouldNotLogToWarnIfInfoIsDisabled() {
        when(log.isWarnEnabled()).thenReturn(false);
        writer.warn(MESSAGE);
        verify(log, never()).info(MESSAGE);
    }

    @Test
    @DisplayName("should log to error if error is enabled")
    void shouldLogToErrorIfInfoIsEnabled() {
        when(log.isErrorEnabled()).thenReturn(true);
        writer.error(MESSAGE);
        verify(log).error(MESSAGE);
    }

    @Test
    @DisplayName("should not log to error if error is disabled")
    void shouldNotLogToErrorIfInfoIsDisabled() {
        when(log.isErrorEnabled()).thenReturn(false);
        writer.error(MESSAGE);
        verify(log, never()).info(MESSAGE);
    }

    @Test
    @DisplayName("should log to debug if debug is enabled")
    void shouldLogToDebugIfInfoIsEnabled() {
        when(log.isDebugEnabled()).thenReturn(true);
        writer.debug(MESSAGE);
        verify(log).debug(MESSAGE);
    }

    @Test
    @DisplayName("should not log to debug if debug is disabled")
    void shouldNotLogToDebugIfInfoIsDisabled() {
        when(log.isDebugEnabled()).thenReturn(false);
        writer.debug(MESSAGE);
        verify(log, never()).info(MESSAGE);
    }

    @Test
    @DisplayName("should write into log with returned PrintWriter")
    void shouldWriteIntoLogWithReturnPrintWriter() {
        try (var pw = writer.printWriter()) {
            pw.print("hello");
        }
        verify(log).info("hello");
    }

}
