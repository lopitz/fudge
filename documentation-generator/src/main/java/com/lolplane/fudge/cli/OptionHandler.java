package com.lolplane.fudge.cli;

import org.apache.commons.cli.CommandLine;

public interface OptionHandler {

    default int priority() {
        return Integer.MIN_VALUE;
    }

    ProgramConfiguration handleCommandLine(CommandLine commandLine, ProgramConfiguration currentConfiguration);
}
