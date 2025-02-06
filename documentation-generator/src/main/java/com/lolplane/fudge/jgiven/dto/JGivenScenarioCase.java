package com.lolplane.fudge.jgiven.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record JGivenScenarioCase(@JsonProperty("caseNr") int caseNumber, List<JGivenStep> steps, List<String> explicitArguments, List<String> derivedArguments,
                                 String status, long durationInNanos) {
}
