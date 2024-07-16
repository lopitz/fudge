package de.opitz.poc.featuredoc.jgiven.dto;

import java.util.List;
import java.util.Map;

public record JGivenTestClass(String className, String name, List<JGivenScenario> scenarios, Map<String, JGivenTag> tagMap) {
}