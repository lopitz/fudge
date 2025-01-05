package com.lolplane.fudge.cli;

import org.apache.commons.cli.CommandLine;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OptionHandlerTest {

    @Test
    @DisplayName("should return Integer.MIN_VALUE as default priority")
    void shouldReturnIntegerMinValueAsDefaultPriority() {
        var handler = new OptionHandler() {
            @Override
            public ProgramConfiguration handleCommandLine(CommandLine commandLine, ProgramConfiguration config) {
                return config;
            }
        };
        assertThat(handler.priority()).isEqualTo(Integer.MIN_VALUE);
    }
}
