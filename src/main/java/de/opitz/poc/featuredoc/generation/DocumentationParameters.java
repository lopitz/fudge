package de.opitz.poc.featuredoc.generation;

import java.nio.file.Path;

public record DocumentationParameters(
    Path sourceRootPath,
    Path targetPath,
    String featuresIndexTemplate,
    String featureOverviewTemplate,
    String scenarioTemplate) {
}
