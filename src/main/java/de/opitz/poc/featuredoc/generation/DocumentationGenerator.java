package de.opitz.poc.featuredoc.generation;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import com.github.mustachejava.DefaultMustacheFactory;
import de.opitz.poc.featuredoc.generation.dto.Feature;
import de.opitz.poc.featuredoc.generation.dto.Scenario;
import de.opitz.poc.featuredoc.jgiven.JGivenJsonParser;
import de.opitz.poc.featuredoc.jgiven.dto.JGivenReport;
import de.opitz.poc.featuredoc.jgiven.dto.JGivenTag;
import de.opitz.poc.featuredoc.jgiven.dto.JGivenTestClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.mapstruct.factory.Mappers;

@Slf4j
public class DocumentationGenerator {

    private static final String SPECIAL_FILESYSTEM_CHARACTERS = "[\\\\/?`'\"]";

    private final JGivenJsonParser jgivenParser;
    private final FileSystem fileSystem;
    private final ScenarioToTestMapper scenarioMapper = Mappers.getMapper(ScenarioToTestMapper.class);

    public DocumentationGenerator(JGivenJsonParser jgivenParser) {
        this(jgivenParser, FileSystems.getDefault());
    }

    public DocumentationGenerator(JGivenJsonParser jgivenParser, FileSystem fileSystem) {
        this.jgivenParser = jgivenParser;
        this.fileSystem = fileSystem;
    }

    public void generateDocumentation(DocumentationParameters documentationParameters) throws IOException {
        var rootTargetPath = Optional.ofNullable(documentationParameters.targetPath()).orElse(fileSystem.getPath("target", "feature-documentation"));
        var sourcePath = Optional.ofNullable(documentationParameters.sourceRootPath()).orElse(fileSystem.getPath("target", "jgiven-reports"));
        var target = Files.createDirectories(rootTargetPath);
        var mustacheFactory = new DefaultMustacheFactory();
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
            buildFeatureIndex(featureDtos, mustacheFactory, target, findFeaturesIndexTemplate(documentationParameters));
            buildFeatures(featureDtos, mustacheFactory, target, findFeatureTemplate(documentationParameters));
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
            var templatePath = fileSystem.getPath(templatePathName);
            if (Files.isReadable(templatePath) && Files.isRegularFile(templatePath)) {
                return Files.newInputStream(templatePath);
            }
        }
        return ClassLoader.getSystemClassLoader().getResource(defaultTemplateName).openStream();
    }

    private List<Feature> buildFeatureDtos(List<JGivenTag> features, Map<String, String> folderMapping, JGivenReport report) {
        return features
            .stream()
            .map(tag -> new Feature(tag.value(), tag.description(), folderMapping.get(tag.value()), buildScenarios(tag, report)))
            .toList();
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
        var result = features
            .stream()
            .map(feature -> Pair.of(feature.value(), buildFeatureFolderName(feature.value(), rootTargetPath)))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        result.values().forEach(folderName -> buildFeatureFolder(rootTargetPath, folderName));
        return result;
    }

    private String buildFeatureFolderName(String featureName, Path rootTargetPath) {
        var folderName = StringUtils.abbreviateMiddle(featureName.replaceAll(SPECIAL_FILESYSTEM_CHARACTERS, "_"), "...", 250);
        if (createFolder(featureName, rootTargetPath, folderName)) {
            return folderName;
        }
        var uuidBasedName = UUID.randomUUID().toString();
        createFolder(featureName, rootTargetPath, uuidBasedName);
        return uuidBasedName;
    }

    private boolean createFolder(String featureName, Path rootTargetPath, String folderName) {
        try {
            Files.createDirectories(fileSystem.getPath(rootTargetPath.toString(), folderName));
            return true;
        } catch (IOException e) {
            log.warn("Error creating feature folder for feature {} using name {}", featureName, folderName, e);
        }
        return false;
    }

    private Path buildFeatureFolder(Path rootTargetPath, String folderName) {
        try {
            return Files.createDirectories(fileSystem.getPath(rootTargetPath.toString(), folderName));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void buildFeatureIndex(
        List<Feature> features,
        DefaultMustacheFactory mustacheFactory,
        Path targetRootPath,
        InputStream featuresIndexTemplate
    ) {
        try (var templateReader = new InputStreamReader(featuresIndexTemplate)) {
            var mustache = mustacheFactory.compile(templateReader, "features-index");
            var variables = Map.of(
                "targetPath", targetRootPath.toString(),
                "features", features);
            var writer = new StringWriter();
            mustache.execute(writer, variables);
            Files.writeString(fileSystem.getPath(targetRootPath.toString(), "index.md"), writer.toString());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void buildFeatures(List<Feature> featureDtos, DefaultMustacheFactory mustacheFactory, Path target, InputStream featureTemplate) {
        try (var templateReader = new InputStreamReader(featureTemplate)) {
            var mustache = mustacheFactory.compile(templateReader, "features-index");
            featureDtos.forEach(feature -> {
                var variables = Map.of("feature", feature);
                var writer = new StringWriter();
                mustache.execute(writer, variables);
                var targetFilePath = fileSystem.getPath(target.toString(), feature.featureFolder(), "index.md");
                try {
                    Files.writeString(targetFilePath, writer.toString());
                } catch (IOException e) {
                    log.error("Error writing documentation page for feature {} to {}", feature.name(), targetFilePath, e);
                }
            });
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static URL mapToUrl(URI uri) {
        try {
            return uri.toURL();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
