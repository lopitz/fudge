package com.lolplane.fudge.cli;

import com.lolplane.fudge.ConsoleWriter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;

@RequiredArgsConstructor
public class HelpOption implements OptionHandler {

    private final ConsoleWriter consoleWriter;

    @Override
    public int priority() {
        return Integer.MAX_VALUE;
    }

    @Override
    public ProgramConfiguration handleCommandLine(CommandLine commandLine, ProgramConfiguration currentConfiguration) {
        if (commandLine.hasOption(CommandLineOptions.helpOption())) {
            var formatter = new HelpFormatter();
            formatter.printHelp(consoleWriter.printWriter(), HelpFormatter.DEFAULT_WIDTH, "FeatureDocumentationGenerator", null,
                CommandLineOptions.buildCommandLineOptions(), formatter.getLeftPadding(), formatter.getDescPadding(), null, false);
            return currentConfiguration.withHelpRequested(true);
        }
        return currentConfiguration;
    }

}
