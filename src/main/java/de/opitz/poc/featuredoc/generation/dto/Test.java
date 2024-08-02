package de.opitz.poc.featuredoc.generation.dto;

import java.util.List;

public record Test(String description, List<String> givenLines, List<String> whenLines, List<String> thenLines) {
}
