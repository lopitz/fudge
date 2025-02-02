package com.lolplane.fudge.cli;

import java.io.IOException;

import com.lolplane.fudge.ConsoleWriter;
import com.lolplane.fudge.tools.LineBuffer;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class VerboseOptionHandlerTest {

    private final Option option = CommandLineOptions.verboseOption();

    private ConsoleWriter consoleWriter;
    private LineBuffer lineBuffer;

    @BeforeEach
    void setUp() {
        lineBuffer = new LineBuffer();
        consoleWriter = new ConsoleWriter(lineBuffer.printWriter());
    }

    @AfterEach
    void tearDown() throws Exception {
        lineBuffer.close();
        consoleWriter.close();
    }

    @Test
    @DisplayName("should enable verbose mode in application configuration if option is given")
    void shouldEnableVerboseModeInApplicationConfigurationIfOptionIsGiven() throws ParseException {
        var commandLine = new DefaultParser().parse(new Options().addOption(option), new String[]{"-" + option.getOpt()});

        var actual = new VerboseOptionHandler(consoleWriter).handleCommandLine(commandLine, ProgramConfiguration.empty());

        assertThat(actual.verboseModeEnabled()).isTrue();
    }

    @Test
    @DisplayName("should have a middle priority")
    void shouldHaveAMiddlePriority() {
        var actual = new VerboseOptionHandler(consoleWriter);

        assertThat(actual.priority()).isZero();
    }

    @Test
    @DisplayName("should not enable verbose mode application configuration if option is not given")
    void shouldNotSetDryRunInApplicationConfigurationIfOptionIsNotGiven() throws ParseException {
        var commandLine = new DefaultParser().parse(new Options().addOption(option), new String[]{});

        var actual = new VerboseOptionHandler(consoleWriter).handleCommandLine(commandLine, ProgramConfiguration.empty());

        assertThat(actual.verboseModeEnabled()).isFalse();
    }

    @Test
    @DisplayName("should write information message of enabled verbose run if option is given")
    void shouldWriteInformationMessageOfEnabledVerboseRunIfOptionIsGiven() throws ParseException, IOException {
        var commandLine = new DefaultParser().parse(new Options().addOption(option), new String[]{"-" + option.getOpt()});

        new VerboseOptionHandler(consoleWriter).handleCommandLine(commandLine, ProgramConfiguration.empty());

        assertThat(lineBuffer.lines()).containsExactly(
            "",
            "The verbose mode has been enabled.",
            "The program will generate a lot of messages.",
            "");
    }

}
