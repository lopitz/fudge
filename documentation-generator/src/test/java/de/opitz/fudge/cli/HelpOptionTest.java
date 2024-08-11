package de.opitz.fudge.cli;

import de.opitz.fudge.ConsoleWriter;
import de.opitz.tools.LineBuffer;
import lombok.SneakyThrows;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HelpOptionTest {

    private ConsoleWriter consoleWriter;
    private LineBuffer lineBuffer;

    @BeforeEach
    void setupTest() {
        lineBuffer = new LineBuffer();
        consoleWriter = new ConsoleWriter(lineBuffer.printWriter());
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
    @DisplayName("should print out help")
    void shouldPrintOutHelp() {
        var commandLine = new DefaultParser().parse(new Options().addOption(CommandLineOptions.helpOption()), new String[]{"-h"});
        new HelpOption(consoleWriter).handleCommandLine(commandLine, ProgramConfiguration.empty());

        assertThat(lineBuffer.lines()).containsExactly(
            "usage: FeatureDocumentationGenerator",
            " -d,--debug          print debug message",
            " -h,--help           print this message",
            " -i,--info           print info message",
            " -n,--dry-run        dry run - doesn't write anything but analyzes the",
            "                     found data",
            " -s,--source <arg>   defines the directory, where the JGiven json report",
            "                     files can be found",
            " -t,--target <arg>   defines the target directory");
    }

}
