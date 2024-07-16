package de.opitz.poc.featuredoc.jgiven.dto;

import java.util.List;

public record JGivenScenario(
    String className,
    String testMethodName,
    String description,
    List<String> tagIds,
    List<String> explicitParameters,
    List<String> derivedParameters,
    List<JGivenScenarioCase> scenarioCases,
    boolean casesAsTable,
    long durationInNanos
) {
}
