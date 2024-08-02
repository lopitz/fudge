package de.opitz.poc.featuredoc.generation;

import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.util.stream.Collectors;

import com.google.common.jimfs.Jimfs;
import de.opitz.poc.featuredoc.jgiven.JGivenJsonParser;
import de.opitz.poc.featuredoc.jgiven.TestIOUtils;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DocumentationGeneratorTest {

    @SneakyThrows
    @Test
    @DisplayName("should build feature index file")
    void shouldBuildFeatureIndexFile() {
        var parser = new JGivenJsonParser();
        var fileSystem = prepareFileSystem();
        var generator = new DocumentationGenerator(parser, fileSystem);
        generator.generateDocumentation(new DocumentationParameters(fileSystem.getPath("target", "jgiven-reports"), null, null));

        var result = Files.lines(fileSystem.getPath("target", "feature-documentation", "index.md")).collect(Collectors.joining("\n"));
        assertThat(result).isNotEmpty().contains("[yearly limit]");
    }

    @SneakyThrows
    private static FileSystem prepareFileSystem() {
        var fileSystem = Jimfs.newFileSystem();

        var reportFolder = Files.createDirectories(fileSystem.getPath("target", "jgiven-reports", "json"));
        var sourceText = TestIOUtils.loadTextFile("jgiven-report.json");
        Files.writeString(fileSystem.getPath(reportFolder.toString(), "de.opitz.poc.featuredoc.features.limit.LimitTests.json"), sourceText);

        return fileSystem;
    }

}