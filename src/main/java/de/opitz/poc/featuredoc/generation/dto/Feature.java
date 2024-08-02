package de.opitz.poc.featuredoc.generation.dto;

import java.util.List;

public record Feature(String name, String description, String featureFolder, List<Scenario> scenarios) {
}
