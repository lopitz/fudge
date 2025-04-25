package com.lolplane.fudge.cli;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import com.lolplane.fudge.ConsoleWriter;
import com.lolplane.fudge.security.PathValidator;
import lombok.RequiredArgsConstructor;
import org.apache.commons.cli.CommandLine;

@RequiredArgsConstructor
public class SourceOptionHandler implements OptionHandler {

    private final ConsoleWriter consoleWriter;

    @Override
    public ProgramConfiguration handleCommandLine(CommandLine commandLine, ProgramConfiguration currentConfiguration) {
        var optionValue = commandLine.getOptionValue(CommandLineOptions.sourceOption());
        if (optionValue == null) {
            return currentConfiguration;
        }

        return preventTraversalAttacks(currentConfiguration.sourceFileSystem().getPath(optionValue))
            .filter(this::checkThatSourceFolderExists)
            .filter(this::checkThatSourceIsOfTypeFolder)
            .map(path -> updateConfiguration(path, currentConfiguration))
            .orElse(currentConfiguration);
    }

    private ProgramConfiguration updateConfiguration(Path sourcePath, ProgramConfiguration currentConfiguration) {
        Path normalizedPath = sourcePath.normalize();
        return currentConfiguration.withSource(normalizedPath);
    }

    private boolean checkThatSourceFolderExists(Path sourcePath) {
        if (Files.exists(sourcePath)) {
            return true;
        }
        consoleWriter.warn("The given source directory [{}] does not exist.", sourcePath.toAbsolutePath().toString());
        return false;
    }

    private boolean checkThatSourceIsOfTypeFolder(Path sourcePath) {
        if (Files.isDirectory(sourcePath)) {
            return true;
        }
        consoleWriter.warn("The given source directory [{}] is not a directory.", sourcePath);
        return false;
    }

    private Optional<Path> preventTraversalAttacks(Path targetPath) {
        if (PathValidator.isPathSafe(targetPath.toString())) {
            return Optional.of(targetPath.normalize());
        }
        consoleWriter.warn("The given target directory [{}] contains potentially unsafe path sequences.", targetPath);
        return Optional.empty();
    }


}
