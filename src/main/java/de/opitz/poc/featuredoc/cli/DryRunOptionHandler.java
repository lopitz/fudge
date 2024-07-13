package de.opitz.poc.featuredoc.cli;

import de.opitz.poc.featuredoc.ConsoleWriter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.cli.CommandLine;

@RequiredArgsConstructor
public class DryRunOptionHandler implements OptionHandler {

    private final ConsoleWriter consoleWriter;

    @Override
    public ProgramConfiguration handleCommandLine(CommandLine commandLine, ProgramConfiguration currentConfiguration) {
        if (commandLine.hasOption(CommandLineOptions.dryRunOption())) {
            writeInformation();
            return currentConfiguration.withDryRun(true);
        }
        return currentConfiguration;
    }

    private void writeInformation() {
        consoleWriter.println();
        consoleWriter.println("The dry run mode has been enabled.");
        consoleWriter.println("The JGiven input data will be analyzed and the feature documentation will be prepared in memory.");
        consoleWriter.println("However, there won't be any changes written to the file system.");
        consoleWriter.println();
    }
}
