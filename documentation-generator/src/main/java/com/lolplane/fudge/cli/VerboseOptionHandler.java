package com.lolplane.fudge.cli;

import com.lolplane.fudge.ConsoleWriter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.cli.CommandLine;

@RequiredArgsConstructor
public class VerboseOptionHandler implements OptionHandler {

    private final ConsoleWriter consoleWriter;

    @Override
    public int priority() {
        return 0;
    }

    @Override
    public ProgramConfiguration handleCommandLine(CommandLine commandLine, ProgramConfiguration currentConfiguration) {
        if (commandLine.hasOption(CommandLineOptions.verboseOption())) {
            writeInformation();
            return currentConfiguration.withVerboseModeEnabled(true);
        }
        return currentConfiguration;
    }

    private void writeInformation() {
        consoleWriter.info("The verbose mode has been enabled.");
        consoleWriter.info("The program will generate a lot of messages.");
    }
}
