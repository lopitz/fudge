package com.lolplane.fudge.cli;

import com.lolplane.fudge.ConsoleWriter;
import lombok.experimental.UtilityClass;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

@UtilityClass
public final class CommandLineOptions {

    private static final Option helpOption = buildHelpOption();
    private static final Option dryRunOption = buildDryRunOption();
    private static final Option sourceOption = buildSourceOption();
    private static final Option targetOption = buildTargetOption();
    private static final Option verboseOption = buildVerboseOption();
    private static final Options options = buildCommandLineOptions();

    private static Option buildHelpOption() {
        return new Option("h", "help", false, "print this message");
    }

    private static Option buildDryRunOption() {
        return new Option("n", "dry-run", false, "dry run - doesn't write anything but analyzes the found data");
    }

    private static Option buildVerboseOption() {
        return new Option("v", "verbose", false, "verbose - prints a lot of debug information");
    }

    private static Option buildSourceOption() {
        return Option
            .builder()
            .required(false)
            .option("s")
            .longOpt("source")
            .hasArg()
            .desc("defines the directory, where the JGiven json report files can be found")
            .build();
    }

    private static Option buildTargetOption() {
        return Option.builder().required(false).option("t").longOpt("target").hasArg().desc("defines the target directory").build();
    }

    private static Options buildCommandLineOptions() {
        Options options = new Options();
        options.addOption(helpOption());
        options.addOption(dryRunOption());
        options.addOption("i", "info", false, "print info message");
        options.addOption(targetOption());
        options.addOption(sourceOption());
        options.addOption(verboseOption());
        return options;
    }

    public static Options options() {
        return options;
    }

    public static Option helpOption() {
        return helpOption;
    }

    public static Option dryRunOption() {
        return dryRunOption;
    }

    public static Option sourceOption() {
        return sourceOption;
    }

    public static Option targetOption() {
        return targetOption;
    }

    public static Option verboseOption() {
        return verboseOption;
    }

    public static void printHelp(ConsoleWriter consoleWriter) {
        var formatter = new HelpFormatter();
        formatter.printHelp(consoleWriter.printWriter(),
            HelpFormatter.DEFAULT_WIDTH,
            "FeatureDocumentationGenerator",
            null,
            CommandLineOptions.buildCommandLineOptions(),
            formatter.getLeftPadding(),
            formatter.getDescPadding(),
            null,
            false);
    }
}
