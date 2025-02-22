package com.lolplane.fudge.cli;

import java.nio.file.Path;

import com.lolplane.fudge.ConsoleWriter;
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
            consoleWriter.warn("The given source directory [{}] does not exist.", sourcePath);
            return currentConfiguration;
        }
        if (!sourceFile.isDirectory()) {
            consoleWriter.warn("The given source directory [{}] is not a directory.", sourcePath);
            return currentConfiguration;
        }
        return currentConfiguration.withSource(sourcePath);
    }
}
