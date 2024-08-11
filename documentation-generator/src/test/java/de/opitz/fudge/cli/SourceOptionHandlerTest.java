package de.opitz.fudge.cli;

import java.nio.file.Path;

import de.opitz.fudge.ConsoleWriter;
import de.opitz.tools.LineBuffer;
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

    private ConsoleWriter consoleWriter;
    private LineBuffer lineBuffer;

    @BeforeEach
    void setUp() {
        lineBuffer = new LineBuffer();
        consoleWriter = new ConsoleWriter(lineBuffer.printWriter());
    }

    @AfterEach
    void tearDown() {
        consoleWriter.close();
    }

    @SneakyThrows
    @Test
    @DisplayName("should set the source directory in the configuration if parameter is given")
    void shouldSetTheSourceDirectoryInTheConfigurationIfParameterIsGiven() {
        var expected = Path.of(".").toAbsolutePath();
        var commandLine = new DefaultParser().parse(new Options().addOption(CommandLineOptions.sourceOption()), new String[]{"-s", expected.toString()});

        var actual = new SourceOptionHandler(consoleWriter).handleCommandLine(commandLine, ProgramConfiguration.empty());

        assertThat(actual.source()).isEqualTo(expected);
    }

    @SneakyThrows
    @Test
    @DisplayName("should not change source and write error message if path is invalid")
    void shouldNotChangeSourceAndWriteErrorMessageIfPathIsInvalid() {
        var commandLine = new DefaultParser().parse(new Options().addOption(CommandLineOptions.sourceOption()), new String[]{"-s", "expected"});

        var actual = new SourceOptionHandler(consoleWriter).handleCommandLine(commandLine, ProgramConfiguration.empty());

        assertThat(actual.source()).isNull();
        assertThat(lineBuffer.lines()).containsExactly("The given source directory [expected] does not exist.");
    }

    @SneakyThrows
    @Test
    @DisplayName("should not change source and write error message if path is not a directory")
    void shouldNotChangeSourceAndWriteErrorMessageIfPathIsNotADirectory() {
        var tempFile = Files.newTemporaryFile();
        var commandLine = new DefaultParser().parse(new Options().addOption(CommandLineOptions.sourceOption()), new String[]{"-s", tempFile.getAbsolutePath()});

        var actual = new SourceOptionHandler(consoleWriter).handleCommandLine(commandLine, ProgramConfiguration.empty());

        assertThat(actual.source()).isNull();
        assertThat(lineBuffer.lines()).containsExactly("The given source directory [%s] is not a directory.".formatted(tempFile.getAbsolutePath()));
    }

}
