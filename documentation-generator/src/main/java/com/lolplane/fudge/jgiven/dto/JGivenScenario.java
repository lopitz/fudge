package com.lolplane.fudge.jgiven.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.With;

@With
@JsonIgnoreProperties(ignoreUnknown = true)
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
