package com.lolplane.fudge;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.lolplane.fudge.cli.CommandLineOptions;
import com.lolplane.fudge.cli.OptionHandler;
import com.lolplane.fudge.cli.ProgramConfiguration;
import com.lolplane.fudge.tools.Either;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.ParseException;
import org.reflections.Reflections;

@Slf4j
public class ProgramConfigurationBuilder {

    private final CommandLineParser parser = new DefaultParser();

    public ProgramConfigurationAndErrors buildProgramConfiguration(ConsoleWriter consoleWriter, String... args) throws ParseException {
        if (args.length == 0) {
            return new ProgramConfigurationAndErrors(ProgramConfiguration.empty().withHelpRequested(true), List.of());
        }
        var commandLine = parser.parse(CommandLineOptions.getCommandLineOptions(), args);
        var constructorsAndErrors = findAllOptionHandlerConstructors();
        var errors = constructorsAndErrors.stream().flatMap(e -> e.left().stream()).toList();
        var constructors = constructorsAndErrors.stream().flatMap(e -> e.right().stream()).toList();
        return new ProgramConfigurationAndErrors(buildProgramConfiguration(constructors, commandLine, consoleWriter), errors);
    }

    private static List<Either<Exception, Constructor<? extends OptionHandler>>> findAllOptionHandlerConstructors() {
        return new Reflections("com.lolplane.fudge.cli")
            .getSubTypesOf(OptionHandler.class)
            .stream()
            .map(ProgramConfigurationBuilder::findConstructor)
            .collect(Collectors.toCollection(ArrayList::new));
    }

    private static ProgramConfiguration buildProgramConfiguration(List<Constructor<? extends OptionHandler>> constructors, CommandLine commandLine,
        ConsoleWriter consoleWriter) {
        return createOptionHandlers(constructors, consoleWriter)
            .sorted(Comparator.comparing(OptionHandler::priority).reversed())
            .reduce(ProgramConfiguration.empty(), (currentConfig, element) -> callOptionHandler(currentConfig, element, commandLine),
                (ignored, right) -> right);
    }

    private static Stream<? extends OptionHandler> createOptionHandlers(List<Constructor<? extends OptionHandler>> constructors, ConsoleWriter
        consoleWriter) {
        return constructors
            .stream()
            .filter(Objects::nonNull)
            .map(constructor -> createInstance(constructor, consoleWriter));
    }

    private static ProgramConfiguration callOptionHandler(ProgramConfiguration currentConfig, OptionHandler element, CommandLine line) {
        if (currentConfig.helpRequested()) {
            return currentConfig;
        }
        var result = element.handleCommandLine(line, currentConfig);
        if (result == null) {
            log.error("The option handler {} returned null instead of a new program configuration. Using the default value for the option.", element
                .getClass()
                .getName());
            return currentConfig;
        }
        return result;
    }

    private static Either<Exception, Constructor<? extends OptionHandler>> findConstructor(Class<? extends OptionHandler> aClass) {
        try {
            var constructor = aClass.getConstructor(ConsoleWriter.class);
            return Either.right(constructor);
        } catch (NoSuchMethodException e) {
            return Either.left(new IllegalArgumentException("The class %s does not define a constructor with a single argument (ConsoleWriter)".formatted(aClass.getName()), e));
        } catch (Exception e) {
            return Either.left(e);
        }
    }

    private static <T extends OptionHandler> T createInstance(Constructor<T> constructor, ConsoleWriter consoleWriter) {
        try {
            return constructor.newInstance(consoleWriter);
        } catch (Exception e) {
            throw new IllegalArgumentException("Unable to instantiate option handler %s".formatted(constructor.getName()), e);
        }
    }
}
