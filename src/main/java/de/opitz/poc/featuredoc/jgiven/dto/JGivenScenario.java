package de.opitz.poc.featuredoc.jgiven.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.With;

@With
public record JGivenScenario(
    String className,
    String testMethodName,
    String description,
    List<String> tagIds,
    @JsonIgnore
    List<JGivenTag> tags,
    List<String> explicitParameters,
    List<String> derivedParameters,
    List<JGivenScenarioCase> scenarioCases,
    boolean casesAsTable,
    long durationInNanos
) {
}
