package de.opitz.fudge.cli;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

public final class CommandLineOptions {

    private static final Option helpOption = buildHelpOption();
    private static final Option dryRunOption = buildDryRunOption();
    private static final Option sourceOption = buildSourceOption();
    private static final Option targetOption = buildTargetOption();

    private CommandLineOptions() {
    }

    private static Option buildHelpOption() {
        return new Option("h", "help", false, "print this message");
    }

    private static Option buildDryRunOption() {
        return new Option("n", "dry-run", false, "dry run - doesn't write anything but analyzes the found data");
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

    public static Options buildCommandLineOptions() {
        Options options = new Options();
        options.addOption(helpOption());
        options.addOption(dryRunOption());
        options.addOption("d", "debug", false, "print debug message");
        options.addOption("i", "info", false, "print info message");
        options.addOption(targetOption());
        options.addOption(sourceOption());
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
}
