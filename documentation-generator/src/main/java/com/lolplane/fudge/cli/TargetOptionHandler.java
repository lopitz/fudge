package com.lolplane.fudge.cli;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.lolplane.fudge.ConsoleWriter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.cli.CommandLine;

@RequiredArgsConstructor
public class TargetOptionHandler implements OptionHandler {

    private final ConsoleWriter consoleWriter;

    @Override
    public ProgramConfiguration handleCommandLine(CommandLine commandLine, ProgramConfiguration currentConfiguration) {
        var optionValue = commandLine.getOptionValue(CommandLineOptions.targetOption());
        if (optionValue == null) {
            return currentConfiguration;
        }
        var targetPath = currentConfiguration.fileSystem().getPath(optionValue);
        return checkThatTargetFolderExistsAndCreateIfNecessary(targetPath, currentConfiguration);
    }

    private ProgramConfiguration checkThatTargetFolderExistsAndCreateIfNecessary(Path targetPath, ProgramConfiguration currentConfiguration) {
        if (Files.exists(targetPath)) {
            return checkTargetFolderIsDirectory(targetPath, currentConfiguration);
        }
        if (currentConfiguration.dryRun()) {
            consoleWriter.printf("The given target directory [%s] did not exist. The dry run mode is enabled, hence it has not been created.%n", targetPath);
            return currentConfiguration;
        }
        try {
            Files.createDirectories(targetPath);
            consoleWriter.printf("The given target directory [%s] did not exist. It's created now.%n", targetPath);
            return currentConfiguration.withTarget(targetPath);
        } catch (IOException e) {
            consoleWriter.printf("The given target directory [%s] did not exist. It could also not be created.%n", targetPath);
            consoleWriter.println("Hence, the option is ignored and the dry run mode has been enabled.");
            consoleWriter.println();
            consoleWriter.println(e.getMessage());
        }
        return currentConfiguration;
    }

    private ProgramConfiguration checkTargetFolderIsDirectory(Path targetPath, ProgramConfiguration currentConfiguration) {
        if (!Files.isDirectory(targetPath)) {
            consoleWriter.printf("The given target directory [%s] is not a directory.%n", targetPath);
            return currentConfiguration;
        }
        return currentConfiguration.withTarget(targetPath);
    }

}
