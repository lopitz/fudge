package com.lolplane.fudge.jgiven.dto;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.With;

@With
@JsonIgnoreProperties(ignoreUnknown = true)
public record JGivenTestClass(String className, String name, List<JGivenScenario> scenarios, Map<String, JGivenTag> tagMap) {
}
