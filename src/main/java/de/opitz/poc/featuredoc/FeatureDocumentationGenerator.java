package de.opitz.poc.featuredoc;

import java.lang.reflect.Constructor;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import de.opitz.poc.featuredoc.cli.CommandLineOptions;
import de.opitz.poc.featuredoc.cli.OptionHandler;
import de.opitz.poc.featuredoc.cli.ProgramConfiguration;
import de.opitz.poc.featuredoc.generation.DocumentationGenerator;
import de.opitz.poc.featuredoc.generation.DocumentationParameters;
import de.opitz.poc.featuredoc.jgiven.JGivenJsonParser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.ParseException;
import org.reflections.Reflections;

@Slf4j
public class FeatureDocumentationGenerator {

    public static void main(String[] args) {
        var consoleWriter = new ConsoleWriter();
        var parser = new DefaultParser();
        try {
            var line = parser.parse(CommandLineOptions.buildCommandLineOptions(), args);
            var constructors = new Reflections("de.opitz.poc.featuredoc.cli")
                .getSubTypesOf(OptionHandler.class)
                .stream()
                .map(FeatureDocumentationGenerator::findConstructor)
                .toList();

            printErrors(constructors);

            var config = constructors
                .stream()
                .filter(e -> Objects.isNull(e.error()))
                .map(ConstructorAndError::constructor)
                .filter(Objects::nonNull)
                .map(constructor -> createInstance(constructor, consoleWriter))
                .sorted(Comparator.comparing(OptionHandler::priority).reversed())
                .reduce(ProgramConfiguration.empty(), (currentConfig, element) -> callOptionHandler(currentConfig, element, line), (_, right) -> right);

            if (config.helpRequested()) {
                return;
            }
            var generator = new DocumentationGenerator(new JGivenJsonParser());
            generator.generateDocumentation(new DocumentationParameters(config.source(), config.target(), null, null, null));
        } catch (ParseException exp) {
            log.error("Parsing failed.  Reason: {}", exp.getMessage(), exp);
        } catch (Exception e) {
            if (e.getCause() != null) {
                log.error("Something went wrong.  Reason: {}", e.getCause().getMessage(), e.getCause());
            } else {
                log.error("Something went wrong.  Reason: {}", e.getMessage(), e);
            }
        }
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

    private static void printErrors(List<? extends ConstructorAndError<? extends OptionHandler>> constructors) {
        constructors
            .stream()
            .filter(constructorAndError -> Objects.nonNull(constructorAndError.error()))
            .forEach(e -> log.warn(e.error().getMessage()));
    }

    private static <T extends OptionHandler> ConstructorAndError<T> findConstructor(Class<T> aClass) {
        try {
            var constructor = aClass.getConstructor(ConsoleWriter.class);
            return new ConstructorAndError<>(aClass, constructor, null);
        } catch (NoSuchMethodException e) {
            return new ConstructorAndError<>(aClass, null, new IllegalArgumentException(("The class %s does not define a constructor with a single argument " +
                "(ConsoleWriter)").formatted(aClass.getName()), e));
        } catch (Exception e) {
            return new ConstructorAndError<>(aClass, null, e);
        }
    }

    private static <T extends OptionHandler> T createInstance(Constructor<T> constructor, ConsoleWriter consoleWriter) {
        try {
            return constructor.newInstance(consoleWriter);
        } catch (Exception e) {
            throw new IllegalArgumentException("Unable to instantiate option handler %s".formatted(constructor.getName()), e);
        }
    }

    private record ConstructorAndError<T>(Class<T> clazz, Constructor<T> constructor, Exception error) {
    }

}
