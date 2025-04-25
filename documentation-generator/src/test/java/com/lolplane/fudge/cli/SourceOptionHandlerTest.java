package com.lolplane.fudge.cli;

import java.nio.file.Path;

import com.lolplane.fudge.PrintWriterConsoleWriter;
import com.lolplane.fudge.tools.LineBuffer;
import lombok.SneakyThrows;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.assertj.core.util.Files;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SourceOptionHandlerTest {

    private PrintWriterConsoleWriter consoleWriter;
    private LineBuffer lineBuffer;

    @BeforeEach
    void setUp() {
        lineBuffer = new LineBuffer();
        consoleWriter = new PrintWriterConsoleWriter(lineBuffer.printWriter());
    }

    @AfterEach
    void tearDown() {
        consoleWriter.close();
    }

    @SneakyThrows
    @Test
    @DisplayName("should set the source directory in the configuration if parameter is given")
    void shouldSetTheSourceDirectoryInTheConfigurationIfParameterIsGiven() {
        var expected = Path.of("").toAbsolutePath();
        var commandLine = new DefaultParser().parse(new Options().addOption(CommandLineOptions.sourceOption()), new String[]{"-s", expected.toString()});

        var actual = new SourceOptionHandler(consoleWriter).handleCommandLine(commandLine, ProgramConfiguration.empty());

        assertThat(actual.source()).isEqualTo(expected);
    }

    @SneakyThrows
    @Test
    @DisplayName("should not change source and write error message if path is invalid")
    void shouldNotChangeSourceAndWriteErrorMessageIfPathIsInvalid() {
        var expected = Path.of("expected").toAbsolutePath();
        var commandLine = new DefaultParser().parse(new Options().addOption(CommandLineOptions.sourceOption()), new String[]{"-s", "expected"});

        var actual = new SourceOptionHandler(consoleWriter).handleCommandLine(commandLine, ProgramConfiguration.empty());

        assertThat(actual.source()).isNull();
        assertThat(lineBuffer.lines()).containsExactly("WARN: The given source directory [%s] does not exist.".formatted(expected.toString()));
    }

    @SneakyThrows
    @Test
    @DisplayName("should not change source and write error message if path is not a directory")
    void shouldNotChangeSourceAndWriteErrorMessageIfPathIsNotADirectory() {
        var tempFile = Files.newTemporaryFile();
        var commandLine = new DefaultParser().parse(new Options().addOption(CommandLineOptions.sourceOption()), new String[]{"-s", tempFile.getAbsolutePath()});

        var actual = new SourceOptionHandler(consoleWriter).handleCommandLine(commandLine, ProgramConfiguration.empty());

        assertThat(actual.source()).isNull();
        assertThat(lineBuffer.lines()).containsExactly("WARN: The given source directory [%s] is not a directory.".formatted(tempFile.getAbsolutePath()));
    }

    @SneakyThrows
    @Test
    @DisplayName("should return given configuration if this option was not set by the command line")
    void shouldReturnGivenConfigurationIfThisOptionWasNotSetByTheCommandLine() {
        var expected = ProgramConfiguration.empty().withSource(Path.of("expected"));

        var commandLine = new DefaultParser().parse(new Options().addOption(CommandLineOptions.sourceOption()), new String[]{});

        var actual = new SourceOptionHandler(consoleWriter).handleCommandLine(commandLine, expected);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("should reject potentially unsafe source folders")
    void shouldRejectPotentiallyUnsafeSourceFolders() throws Exception {
        var tempFile = Path.of("../../../etc/passwd");
        var commandLine = new DefaultParser().parse(new Options().addOption(CommandLineOptions.sourceOption()), new String[]{"-s", tempFile.toString()});

        var actual = new SourceOptionHandler(consoleWriter).handleCommandLine(commandLine, ProgramConfiguration.empty());

        assertThat(actual.source()).isNull();
        assertThat(lineBuffer.lines()).containsExactly("WARN: The given target directory [%s] contains potentially unsafe path sequences.".formatted(tempFile.toString()));
    }

}
