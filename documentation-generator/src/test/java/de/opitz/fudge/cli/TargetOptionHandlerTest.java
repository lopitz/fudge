package de.opitz.fudge.cli;

import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.UUID;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
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

class TargetOptionHandlerTest {

    private final FileSystem fileSystem = Jimfs.newFileSystem(Configuration.unix());

    private ConsoleWriter consoleWriter;
    private LineBuffer lineBuffer;
    private ProgramConfiguration initialProgramConfiguration;

    @BeforeEach
    void setUp() {
        lineBuffer = new LineBuffer();
        consoleWriter = new ConsoleWriter(lineBuffer.printWriter());
        initialProgramConfiguration = ProgramConfiguration.empty().withFileSystem(fileSystem);
    }

    @AfterEach
    void deleteTempFile() {
        consoleWriter.close();
    }

    @SneakyThrows
    @Test
    @DisplayName("should set the target directory in the configuration if parameter is given and directory exists")
    void shouldSetTheTargetDirectoryInTheConfigurationIfParameterIsGivenAndDirectoryExists() {
        var expected = Files.createDirectory(fileSystem.getPath("temp")).toAbsolutePath();
        var commandLine = new DefaultParser().parse(new Options().addOption(CommandLineOptions.targetOption()), new String[]{"-t", expected.toString()});

        var actual = new TargetOptionHandler(consoleWriter).handleCommandLine(commandLine, initialProgramConfiguration);

        assertThat(actual.target()).isNotNull().hasToString(expected.toString());
    }

    @SneakyThrows
    @Test
    @DisplayName("should change target, write info message and create directory if path does not exist")
    void shouldChangeTargetWriteInfoMessageIfPathDoesNotExist() {
        var folderName = UUID.randomUUID().toString();
        var expectedFolder = fileSystem.getPath(folderName).toAbsolutePath();
        var commandLine = new DefaultParser().parse(new Options().addOption(CommandLineOptions.targetOption()), new String[]{"-t", expectedFolder.toString()});

        var actual = new TargetOptionHandler(consoleWriter).handleCommandLine(commandLine, initialProgramConfiguration);

        assertThat(Files.exists(expectedFolder)).isTrue();
        assertThat(actual.target()).isEqualTo(expectedFolder);
        assertThat(lineBuffer.lines()).containsExactly("The given target directory [%s] did not exist. It's created now.".formatted(expectedFolder));
    }

    @SneakyThrows
    @Test
    @DisplayName("should not change target and write info message not creating directory if path does not exist and dry run is enabled")
    void shouldNotChangeTargetAndWriteInfoMessageIfPathDoesNotExist() {
        var folderName = UUID.randomUUID().toString();
        var expectedFolder = fileSystem.getPath(folderName).toAbsolutePath();
        var commandLine = new DefaultParser().parse(new Options().addOption(CommandLineOptions.targetOption()), new String[]{"-t", expectedFolder.toString()});

        var actual = new TargetOptionHandler(consoleWriter).handleCommandLine(commandLine, initialProgramConfiguration.withDryRun(true));

        assertThat(expectedFolder).doesNotExist();
        assertThat(actual.target()).isNull();
        assertThat(lineBuffer.lines()).containsExactly(("The given target directory [%s] did not exist. The dry run mode is enabled, hence it has not been " +
            "created.").formatted(expectedFolder));
    }

    @SneakyThrows
    @Test
    @DisplayName("should not change target and write error message if path is not a directory")
    void shouldNotChangeTargetAndWriteErrorMessageIfPathIsNotADirectory() {
        var folderName = UUID.randomUUID().toString();
        var expectedFolder = fileSystem.getPath(folderName).toAbsolutePath();
        Files.createFile(expectedFolder);
        var commandLine = new DefaultParser().parse(new Options().addOption(CommandLineOptions.targetOption()), new String[]{"-t",
            expectedFolder.toAbsolutePath().toString()});

        var actual = new TargetOptionHandler(consoleWriter).handleCommandLine(commandLine, initialProgramConfiguration);

        assertThat(actual.target()).isNull();
        assertThat(lineBuffer.lines()).containsExactly("The given target directory [%s] is not a directory.".formatted(expectedFolder
            .toAbsolutePath()
            .toString()));
    }

    @SneakyThrows
    @Test
    @DisplayName("should not change target, write error message and enable dry run mode if creation of target directory fails")
    void shouldNotChangeTargetWriteErrorMessageAndEnableDryRunModeIfCreationOfTargetDirectoryFails() {
        initialProgramConfiguration = ProgramConfiguration.empty().withFileSystem(FileSystems.getDefault());

        var folderName = "/:&|";
        var expectedFolder = fileSystem.getPath(folderName).toAbsolutePath();
        var commandLine = new DefaultParser().parse(new Options().addOption(CommandLineOptions.targetOption()), new String[]{"-t", expectedFolder.toString()});

        var actual = new TargetOptionHandler(consoleWriter).handleCommandLine(commandLine, initialProgramConfiguration);

        assertThat(actual.target()).isNull();
        assertThat(lineBuffer.lines()).containsExactly(
            "The given target directory [/:&|] did not exist. It could also not be created.",
            "Hence, the option is ignored and the dry run mode has been enabled.",
            "",
            "/:&|: Read-only file system");
    }

}
