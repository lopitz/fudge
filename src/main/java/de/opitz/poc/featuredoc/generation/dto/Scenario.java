package de.opitz.poc.featuredoc.generation.dto;

import java.util.List;

public record Scenario(String description, List<Test> tests) {
}
