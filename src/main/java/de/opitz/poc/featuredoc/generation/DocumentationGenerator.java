package de.opitz.poc.featuredoc.generation;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import com.github.mustachejava.DefaultMustacheFactory;
import de.opitz.poc.featuredoc.generation.dto.Feature;
import de.opitz.poc.featuredoc.generation.dto.Test;
import de.opitz.poc.featuredoc.jgiven.JGivenJsonParser;
import de.opitz.poc.featuredoc.jgiven.dto.JGivenTag;
import org.apache.commons.lang3.tuple.Pair;

public class DocumentationGenerator {

    private final JGivenJsonParser jgivenParser;
    private final FileSystem fileSystem;

    public DocumentationGenerator(JGivenJsonParser jgivenParser) {
        this(jgivenParser, FileSystems.getDefault());
    }

    public DocumentationGenerator(JGivenJsonParser jgivenParser, FileSystem fileSystem) {
        this.jgivenParser = jgivenParser;
        this.fileSystem = fileSystem;
    }

    public void generateDocumentation(DocumentationParameters documentationParameters) throws IOException {
        var rootTargetPath = Optional.ofNullable(documentationParameters.targetPath()).orElse(fileSystem.getPath("target", "feature-documentation"));
        var target = Files.createDirectories(rootTargetPath);
        var mustacheFactory = new DefaultMustacheFactory();
        try (var reportUrls = Files
            .find(documentationParameters.sourceRootPath(), 5, (path, attributes) -> path.toString().endsWith(".json") && attributes.isRegularFile() &&  attributes.size() > 0 && Files.isReadable(path))
            .map(Path::toUri)
            .map(DocumentationGenerator::mapToUrl)) {
            var report = jgivenParser.parseReportFiles(reportUrls);
            var features = report.tags(feature -> Objects.equals(feature.type(), "Feature")).distinct().sorted(Comparator.comparing(JGivenTag::value)).toList();
            var folderMapping = buildFeaturePathStructure(features, target);
            buildFeatureIndex(buildFeatureDtos(features, folderMapping), mustacheFactory, target);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private List<Feature> buildFeatureDtos(List<JGivenTag> features, Map<String, String> folderMapping) {
        return features
            .stream()
            .map(tag -> new Feature(tag.value(), tag.description(), folderMapping.get(tag.value()), buildTests(tag)))
            .toList();
    }

    private List<Test> buildTests(JGivenTag tag) {
        return List.of();
    }

    private Map<String, String> buildFeaturePathStructure(List<JGivenTag> features, Path rootTargetPath) {
        var result = features
            .stream()
            .map(feature -> Pair.of(feature.value(), UUID.randomUUID().toString()))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        result.values().forEach(folderName -> buildFeatureFolder(rootTargetPath, folderName));
        return result;
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
        Path targetRootPath
    ) {
        try (var templateReader = new InputStreamReader(ClassLoader.getSystemClassLoader().getResource("templates/FeaturesIndexPage.md").openStream())) {
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

    private static URL mapToUrl(URI uri) {
        try {
            return uri.toURL();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}
