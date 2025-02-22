package com.lolplane.fudge.cli;

import com.lolplane.fudge.PrintWriterConsoleWriter;
import com.lolplane.fudge.tools.LineBuffer;
import lombok.SneakyThrows;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HelpOptionTest {

    private PrintWriterConsoleWriter consoleWriter;

    @BeforeEach
    void setupTest() {
        var lineBuffer = new LineBuffer();
        consoleWriter = new PrintWriterConsoleWriter(lineBuffer.printWriter());
    }

    @AfterEach
    void tearDown() {
        consoleWriter.close();
    }

    @SneakyThrows
    @Test
    @DisplayName("should add help option to program configuration in case help is requested")
    void shouldAddHelpOptionToProgramConfiguration() {
        var commandLine = new DefaultParser().parse(new Options().addOption(CommandLineOptions.helpOption()), new String[]{"-h"});

        var actual = new HelpOption(consoleWriter).handleCommandLine(commandLine, ProgramConfiguration.empty());

        assertThat(actual.helpRequested()).isTrue();
    }

    @SneakyThrows
    @Test
    @DisplayName("should not add help option to program configuration in case help is not requested")
    void shouldNotAddHelpOptionToProgramConfigurationInCaseHelpIsNotRequested() {
        var commandLine = new DefaultParser().parse(new Options().addOption(CommandLineOptions.helpOption()), new String[]{});

        var actual = new HelpOption(consoleWriter).handleCommandLine(commandLine, ProgramConfiguration.empty());

        assertThat(actual.helpRequested()).isFalse();
    }

    @SneakyThrows
    @Test
    @DisplayName("should have highest priority")
    void shouldHaveHighestPriority() {
        var actual = new HelpOption(consoleWriter);

        assertThat(actual.priority()).isEqualTo(Integer.MAX_VALUE);
    }

}
