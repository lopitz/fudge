package com.lolplane.fudge.generation;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.lolplane.fudge.ConsoleWriter;
import com.lolplane.fudge.generation.dto.ConnectedIssue;
import com.lolplane.fudge.generation.dto.Feature;
import com.lolplane.fudge.generation.dto.Scenario;
import com.lolplane.fudge.jgiven.JGivenJsonParser;
import com.lolplane.fudge.jgiven.dto.JGivenReport;
import com.lolplane.fudge.jgiven.dto.JGivenTag;
import com.lolplane.fudge.jgiven.dto.JGivenTestClass;
import org.apache.commons.lang3.tuple.Pair;
import org.mapstruct.factory.Mappers;

public class DocumentationGenerator {

    private final ConsoleWriter consoleWriter;
    private final JGivenJsonParser jgivenParser;
    private final FileSystem fileSystem;
    private final ScenarioToTestMapper scenarioMapper = Mappers.getMapper(ScenarioToTestMapper.class);
    private final FolderCreator folderCreator;
    private final MustacheFactory mustacheFactory;
    private final PathNameCreator pathNameCreator = new PathNameCreator();

    public DocumentationGenerator(ConsoleWriter consoleWriter, JGivenJsonParser jgivenParser, FileSystem fileSystem) {
        this.consoleWriter = consoleWriter;
        this.jgivenParser = jgivenParser;
        this.fileSystem = fileSystem;
        this.folderCreator = new FolderCreator(consoleWriter, fileSystem);

        // Initialize Mustache factory
        // Note: HTML escaping is handled in the escapeVariables method
        this.mustacheFactory = new DefaultMustacheFactory();
    }

    public void generateDocumentation(DocumentationParameters documentationParameters) throws IOException {
        var targetRootPath = Optional.ofNullable(documentationParameters.targetPath()).orElse(fileSystem.getPath("target", "feature-documentation"));
        var sourcePath = Optional.ofNullable(documentationParameters.sourceRootPath()).orElse(fileSystem.getPath("target", "jgiven-reports"));
        var target = Files.createDirectories(targetRootPath);
        try (var reportUrls = Files
            .find(sourcePath, 5, (path, attributes) -> path
                .toString()
                .endsWith(".json") && attributes.isRegularFile() && attributes.size() > 0 && Files.isReadable(path))
            .map(Path::toUri)
            .map(DocumentationGenerator::mapToUrl)
        ) {
            var report = jgivenParser.parseReportFiles(reportUrls);
            var features = report.tags(feature -> Objects.equals(feature.type(), "Feature")).distinct().sorted(Comparator.comparing(JGivenTag::value)).toList();
            var folderMapping = buildFeaturePathStructure(features, target);
            var featureDtos = buildFeatureDtos(features, folderMapping, report);
            buildFeatureIndex(featureDtos, target, findFeaturesIndexTemplate(documentationParameters));
            buildFeatures(featureDtos, findFeatureTemplate(documentationParameters), documentationParameters, targetRootPath);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private InputStream findFeaturesIndexTemplate(DocumentationParameters documentationParameters) throws IOException {
        return openTemplate(documentationParameters.featuresIndexTemplate(), "templates/FeaturesIndexPage.md");
    }

    private InputStream findFeatureTemplate(DocumentationParameters documentationParameters) throws IOException {
        return openTemplate(documentationParameters.featuresIndexTemplate(), "templates/FeatureTemplate.md");
    }

    private InputStream openTemplate(String templatePathName, String defaultTemplateName) throws IOException {
        if (templatePathName != null) {
            // Validate path to prevent path traversal attacks
            if (!com.lolplane.fudge.security.PathValidator.isPathSafe(templatePathName)) {
                consoleWriter.warn("The template path [{}] contains potentially unsafe path sequences.", templatePathName);
                // Fall back to default template
            } else {
                var templatePath = fileSystem.getPath(templatePathName);
                // Normalize the path to prevent path traversal attacks
                templatePath = templatePath.normalize();

                if (Files.isReadable(templatePath) && Files.isRegularFile(templatePath)) {
                    InputStream inputStream = Files.newInputStream(templatePath);

                    // Validate template content for potentially malicious content
                    if (!validateTemplateContent(inputStream, templatePath.toString())) {
                        // Fall back to default template
                        consoleWriter.warn("The template [{}] contains potentially malicious content.", templatePath);
                    } else {
                        // Reopen the stream since it was consumed during validation
                        return Files.newInputStream(templatePath);
                    }
                }
            }
        }

        return loadTemplateWithClassLoader(ClassLoader.getSystemClassLoader(), defaultTemplateName)
            .or(() -> loadTemplateWithClassLoader(DocumentationGenerator.class.getClassLoader(), defaultTemplateName))
            .map(this::openTemplate)
            .orElseThrow(() -> new UncheckedIOException(new FileNotFoundException(
                "Template [%s] was not found. Please specify a valid URL where the template can be found at.".formatted(defaultTemplateName))));
    }

    private InputStream openTemplate(URL url) {
        // Validate URL to ensure secure handling of external resources
        if (!com.lolplane.fudge.security.URLValidator.isURLSafe(url)) {
            throw new UncheckedIOException(new IOException("The URL [%s] is not allowed.".formatted(url.toString())));
        }

        try {
            InputStream inputStream = url.openStream();

            // Validate template content for potentially malicious content
            if (!validateTemplateContent(inputStream, url.toString())) {
                throw new UncheckedIOException(new IOException("The template at URL [%s] contains potentially malicious content.".formatted(url.toString())));
            }

            // Reopen the stream since it was consumed during validation
            return url.openStream();
        } catch (IOException e) {
            throw new UncheckedIOException("Error opening template at URL [%s]".formatted(url.toString()), e);
        }
    }

    private boolean validateTemplateContent(InputStream inputStream, String source) {
        try {
            return com.lolplane.fudge.security.TemplateValidator.isTemplateSafe(inputStream);
        } catch (IOException e) {
            consoleWriter.warn("Error validating template content from [{}]: {}", source, e.getMessage());
            return false;
        }
    }

    private Optional<URL> loadTemplateWithClassLoader(ClassLoader classLoader, String template) {
        try {
            return Optional.ofNullable(classLoader.getResource(template));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private List<Feature> buildFeatureDtos(List<JGivenTag> features, Map<String, String> folderMapping, JGivenReport report) {
        return features
            .stream()
            .map(featureTag -> buildFeature(folderMapping, report, featureTag))
            .toList();
    }

    private Feature buildFeature(Map<String, String> folderMapping, JGivenReport report, JGivenTag featureTag) {
        var scenarios = buildScenarios(featureTag, report);
        var epics = getConnectedIssues(scenarios, Scenario::epics);
        var stories = getConnectedIssues(scenarios, Scenario::stories);
        var featurePath = folderMapping.get(featureTag.value());
        return new Feature(featureTag.value(), featureTag.description(), featurePath, scenarios, epics, stories);
    }

    private static List<ConnectedIssue> getConnectedIssues(List<Scenario> scenarios, Function<Scenario, List<ConnectedIssue>> propertyExtractor) {
        return scenarios.stream().map(propertyExtractor).flatMap(Collection::stream).distinct().sorted(Comparator.comparing(ConnectedIssue::id)).toList();
    }

    private List<Scenario> buildScenarios(JGivenTag tag, JGivenReport report) {
        var idGenerator = IdGenerator.ofInt(1);
        return report
            .testClasses(test -> test.tagMap().containsValue(tag))
            .map(JGivenTestClass::scenarios)
            .flatMap(Collection::stream)
            .map(jGivenScenario -> scenarioMapper.map(jGivenScenario, idGenerator.iterator()))
            .toList();
    }

    private Map<String, String> buildFeaturePathStructure(List<JGivenTag> features, Path rootTargetPath) {
        return features
            .stream()
            .map(feature -> Pair.of(feature.value(), rootTargetPath.relativize(folderCreator.createFolder(feature.value(), rootTargetPath)).toString()))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private void buildFeatureIndex(
        List<Feature> features,
        Path targetRootPath,
        InputStream featuresIndexTemplate
    ) {
        var variables = Map.<String, Object>of("features", features);
        var resultPath = fileSystem.getPath(targetRootPath.toString(), "index.md");
        generateTargetFileWithTemplateEngine(featuresIndexTemplate, variables, resultPath);
        consoleWriter.debug("Generated documentation index at {}", resultPath);
    }

    private void generateTargetFileWithTemplateEngine(InputStream template, Map<String, Object> variables, Path resultPath) {
        try (var templateReader = new InputStreamReader(template)) {
            var mustache = mustacheFactory.compile(templateReader, "features-index");
            var writer = new StringWriter();

            // Escape user-provided content to prevent XSS attacks
            Map<String, Object> escapedVariables = escapeVariables(variables);

            mustache.execute(writer, escapedVariables);
            Files.writeString(resultPath, writer.toString());
            consoleWriter.debug("Generated documentation page at {}", resultPath);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Escapes user-provided content in variables to prevent XSS attacks.
     *
     * @param variables The variables to escape
     * @return The escaped variables
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> escapeVariables(Map<String, Object> variables) {
        Map<String, Object> result = new HashMap<>(variables.size());

        for (Map.Entry<String, Object> entry : variables.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if (value instanceof String) {
                // Escape string values
                result.put(key, com.lolplane.fudge.security.HTMLEscaper.escapeHtml((String) value));
            } else if (value instanceof Collection) {
                // Recursively escape collections
                Collection<Object> collection = (Collection<Object>) value;
                List<Object> escapedCollection = new ArrayList<>(collection.size());

                for (Object item : collection) {
                    if (item instanceof Map) {
                        // Recursively escape maps in collections
                        escapedCollection.add(escapeVariables((Map<String, Object>) item));
                    } else if (item instanceof String) {
                        // Escape string values in collections
                        escapedCollection.add(com.lolplane.fudge.security.HTMLEscaper.escapeHtml((String) item));
                    } else {
                        // Keep other values as is
                        escapedCollection.add(item);
                    }
                }

                result.put(key, escapedCollection);
            } else if (value instanceof Map) {
                // Recursively escape maps
                result.put(key, escapeVariables((Map<String, Object>) value));
            } else {
                // Keep other values as is
                result.put(key, value);
            }
        }

        return result;
    }

    private void buildFeatures(List<Feature> featureDtos, InputStream featureTemplate, DocumentationParameters documentationParameters, Path targetRootPath) {
        try (var templateReader = new InputStreamReader(featureTemplate)) {
            var mustache = mustacheFactory.compile(templateReader, "feature-index");
            featureDtos.forEach(feature -> buildFeature(feature, mustache, documentationParameters, targetRootPath));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void buildFeature(Feature feature, Mustache mustache, DocumentationParameters documentationParameters, Path targetRootPath) {
        var enrichedScenarios = feature
            .scenarios()
            .stream()
            .map(scenario -> generateFileName(scenario, feature.featureFolder(), feature))
            .map(scenarioAndFileName -> buildScenarioFile(scenarioAndFileName, documentationParameters, targetRootPath))
            .map(scenarioAndFileName -> scenarioAndFileName.scenario().withFileName(scenarioAndFileName.fileName()))
            .toList();
        var variables = Map.of("feature", feature.withScenarios(enrichedScenarios));
        var writer = new StringWriter();
        mustache.execute(writer, variables);
        var targetFilePath = fileSystem.getPath(targetRootPath.toString(), feature.featureFolder(), "index.md");
        try {
            Files.writeString(targetFilePath, writer.toString());
            consoleWriter.debug("Generated documentation page for feature {} at {}", feature.name(), targetFilePath);
        } catch (IOException e) {
            consoleWriter.error("Error writing documentation page for feature {} to {}", feature.name(), targetFilePath, e);
        }
    }

    private ScenarioAndFileName generateFileName(Scenario scenario, String parentPath, Feature feature) {
        var fileName = "%04d-%s.md".formatted(scenario.index(), pathNameCreator.createPathName(scenario.description()));
        return new ScenarioAndFileName(feature, scenario, fileName, fileSystem.getPath(parentPath, fileName));
    }

    private ScenarioAndFileName buildScenarioFile(
        ScenarioAndFileName scenarioAndFileName,
        DocumentationParameters documentationParameters,
        Path targetRootPath) {
        try {
            var variables = Map.of(
                "featureName", scenarioAndFileName.feature().name(),
                "scenario", scenarioAndFileName.scenario()
            );
            generateTargetFileWithTemplateEngine(openTemplate(documentationParameters.scenarioTemplate(), "templates/ScenarioTemplate.md"), variables,
                fileSystem.getPath(targetRootPath.toString(), scenarioAndFileName.filePath().toString()));
        } catch (IOException e) {
            consoleWriter.error("Error writing documentation page for scenario {} to file {}", scenarioAndFileName
                .feature()
                .name(), scenarioAndFileName.fileName(), e);
        }
        return scenarioAndFileName;
    }

    private static URL mapToUrl(URI uri) {
        try {
            return uri.toURL();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private record ScenarioAndFileName(Feature feature, Scenario scenario, String fileName, Path filePath) {
    }
}
