package com.lolplane.fudge;

import java.io.IOException;
import java.util.List;

import com.lolplane.fudge.cli.CommandLineOptions;
import com.lolplane.fudge.cli.ProgramConfiguration;
import com.lolplane.fudge.generation.DocumentationGenerator;
import com.lolplane.fudge.generation.DocumentationParameters;
import com.lolplane.fudge.jgiven.JGivenJsonParser;
import org.apache.commons.cli.ParseException;

public class FeatureDocumentationGenerator {

    @SuppressWarnings("java:S106") // System.out is used here as default on command line
    public static void main(String[] args) {
        parseCommandLineAndGenerateDocumentation(new PrintWriterConsoleWriter(), args);
    }

    public static void parseCommandLineAndGenerateDocumentation(ConsoleWriter consoleWriter, String... args) {
        parseCommandLineAndGenerateDocumentation(consoleWriter, List.of(args));
    }

    public static void parseCommandLineAndGenerateDocumentation(ConsoleWriter consoleWriter, List<String> args) {
        try {
            var config = buildProgramConfiguration(consoleWriter, args);
            if (config.helpRequested()) {
                CommandLineOptions.printHelp(consoleWriter);
                return;
            }
            generateDocumentation(consoleWriter, config);
        } catch (ParseException exp) {
            consoleWriter.error("Parsing failed. Reason: %s".formatted(exp.getMessage()));
        } catch (Exception e) {
            if (e.getCause() != null) {
                consoleWriter.error("Something went wrong. Reason: {}", e.getCause().getMessage(), e.getCause());
            } else {
                consoleWriter.error("Something went wrong. Reason: {}", e.getMessage(), e);
            }
        }
    }

    private static ProgramConfiguration buildProgramConfiguration(ConsoleWriter consoleWriter, List<String> args) throws ParseException {
        var configurationAndErrors = new ProgramConfigurationBuilder(consoleWriter).buildProgramConfiguration(args);
        printErrors(consoleWriter, configurationAndErrors.errors());
        return configurationAndErrors.configuration();
    }

    private static void printErrors(ConsoleWriter consoleWriter, List<Exception> errors) {
        errors.forEach(error -> consoleWriter.error(error.getMessage()));
    }

    private static void generateDocumentation(ConsoleWriter consoleWriter, ProgramConfiguration config) throws IOException {
        if (consoleWriter instanceof PrintWriterConsoleWriter pw) {
            pw.setDebugEnabled(config.verboseModeEnabled());
        }
        var generator = new DocumentationGenerator(consoleWriter, new JGivenJsonParser(consoleWriter), config.fileSystem());
        generator.generateDocumentation(new DocumentationParameters(config.source(), config.target(), null, null, null));
    }

}
