package com.lolplane.fudge.cli;

import com.lolplane.fudge.ConsoleWriter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.cli.CommandLine;

@RequiredArgsConstructor
public class HelpOption implements OptionHandler {

    @SuppressWarnings("unused")
    private final ConsoleWriter consoleWriter;

    @Override
    public int priority() {
        return Integer.MAX_VALUE;
    }

    @Override
    public ProgramConfiguration handleCommandLine(CommandLine commandLine, ProgramConfiguration currentConfiguration) {
        if (commandLine.hasOption(CommandLineOptions.helpOption())) {
            return currentConfiguration.withHelpRequested(true);
        }
        return currentConfiguration;
    }

}
