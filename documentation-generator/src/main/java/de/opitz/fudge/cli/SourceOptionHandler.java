package de.opitz.fudge.cli;

import java.nio.file.Path;

import de.opitz.fudge.ConsoleWriter;
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
        var sourcePath = Path.of(optionValue);
        var sourceFile = sourcePath.toFile();
        if (!sourceFile.exists()) {
            consoleWriter.printf("The given source directory [%s] does not exist.%n", sourcePath);
            return currentConfiguration;
        }
        if (!sourceFile.isDirectory()) {
            consoleWriter.printf("The given source directory [%s] is not a directory.%n", sourcePath);
            return currentConfiguration;
        }
        return currentConfiguration.withSource(sourcePath);
    }
}
