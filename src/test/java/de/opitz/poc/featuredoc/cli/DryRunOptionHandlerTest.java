package de.opitz.poc.featuredoc.cli;

import java.io.IOException;

import de.opitz.poc.featuredoc.ConsoleWriter;
import de.opitz.tools.LineBuffer;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DryRunOptionHandlerTest {

    private final Option option = CommandLineOptions.dryRunOption();

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
    @DisplayName("should set dry run in application configuration if option is given")
    void shouldSetDryRunInApplicationConfigurationIfOptionIsGiven() throws ParseException {
        var commandLine = new DefaultParser().parse(new Options().addOption(option), new String[]{"-" + option.getOpt()});

        var actual = new DryRunOptionHandler(consoleWriter).handleCommandLine(commandLine, ProgramConfiguration.empty());

        assertThat(actual.dryRun()).isTrue();
    }

    @Test
    @DisplayName("should not set dry run in application configuration if option is not given")
    void shouldNotSetDryRunInApplicationConfigurationIfOptionIsNotGiven() throws ParseException {
        var commandLine = new DefaultParser().parse(new Options().addOption(option), new String[]{});

        var actual = new DryRunOptionHandler(consoleWriter).handleCommandLine(commandLine, ProgramConfiguration.empty());

        assertThat(actual.dryRun()).isFalse();
    }

    @Test
    @DisplayName("should write information message of enabled dry run if option is given")
    void shouldWriteInformationMessageOfEnabledDryRunIfOptionIsGiven() throws ParseException, IOException {
        var commandLine = new DefaultParser().parse(new Options().addOption(option), new String[]{"-" + option.getOpt()});

        var actual = new DryRunOptionHandler(consoleWriter).handleCommandLine(commandLine, ProgramConfiguration.empty());

        assertThat(actual.dryRun()).isTrue();
        assertThat(lineBuffer.lines()).containsExactly(
            "",
            "The dry run mode has been enabled.",
            "The JGiven input data will be analyzed and the feature documentation will be prepared in memory.",
            "However, there won't be any changes written to the file system.",
            "");
    }

}
