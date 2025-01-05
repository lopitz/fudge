package com.lolplane.fudge;

import java.io.IOException;
import java.util.List;

import com.lolplane.fudge.cli.CommandLineOptions;
import com.lolplane.fudge.cli.ProgramConfiguration;
import com.lolplane.fudge.generation.DocumentationGenerator;
import com.lolplane.fudge.generation.DocumentationParameters;
import com.lolplane.fudge.jgiven.JGivenJsonParser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.ParseException;

@Slf4j
public class FeatureDocumentationGenerator {

    private static ConsoleWriter consoleWriter;

    @SuppressWarnings("java:S106") // System.out is used here as default on command line
    public static void main(String[] args) {
        consoleWriter = new ConsoleWriter();
        parseCommandLineAndGenerateDocumentation(args);
    }

    private static void parseCommandLineAndGenerateDocumentation(String[] args) {
        try {
            var config = buildProgramConfiguration(args);
            if (config.helpRequested()) {
                CommandLineOptions.printHelp(consoleWriter);
            }
            generateDocumentation(config);
        } catch (ParseException exp) {
            consoleWriter.println("Parsing failed. Reason: %s".formatted(exp.getMessage()));
        } catch (Exception e) {
            if (e.getCause() != null) {
                log.error("Something went wrong.  Reason: {}", e.getCause().getMessage(), e.getCause());
            } else {
                log.error("Something went wrong.  Reason: {}", e.getMessage(), e);
            }
        }
    }

    private static ProgramConfiguration buildProgramConfiguration(String[] args) throws ParseException {
        var configurationAndErrors = new ProgramConfigurationBuilder().buildProgramConfiguration(consoleWriter, args);
        printErrors(configurationAndErrors.errors());
        return configurationAndErrors.configuration();
    }

    private static void printErrors(List<Exception> errors) {
        errors.forEach(error -> consoleWriter.println(error.getMessage()));
    }

    private static void generateDocumentation(ProgramConfiguration config) throws IOException {
        var generator = new DocumentationGenerator(new JGivenJsonParser());
        generator.generateDocumentation(new DocumentationParameters(config.source(), config.target(), null, null, null));
    }

}
