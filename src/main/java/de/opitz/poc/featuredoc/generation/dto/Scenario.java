package de.opitz.poc.featuredoc.generation.dto;

import java.util.List;

public record Scenario(int index, String description, List<Test> tests, Cases cases) {
}
