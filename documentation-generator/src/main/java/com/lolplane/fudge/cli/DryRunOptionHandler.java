package com.lolplane.fudge.cli;

import com.google.common.jimfs.Jimfs;
import com.lolplane.fudge.ConsoleWriter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.cli.CommandLine;

@RequiredArgsConstructor
public class DryRunOptionHandler implements OptionHandler {

    private final ConsoleWriter consoleWriter;

    @Override
    public int priority() {
        return 0;
    }

    @Override
    public ProgramConfiguration handleCommandLine(CommandLine commandLine, ProgramConfiguration currentConfiguration) {
        if (commandLine.hasOption(CommandLineOptions.dryRunOption())) {
            writeInformation();
            return currentConfiguration.withFileSystem(Jimfs.newFileSystem());
        }
        return currentConfiguration;
    }

    private void writeInformation() {
        consoleWriter.info("The dry run mode has been enabled.");
        consoleWriter.info("The JGiven input data will be analyzed and the feature documentation will be prepared in memory.");
        consoleWriter.info("However, there won't be any changes written to the file system.");
    }
}
