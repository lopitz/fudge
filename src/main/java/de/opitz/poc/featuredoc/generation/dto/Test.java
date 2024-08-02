package de.opitz.poc.featuredoc.generation.dto;

import java.util.List;

public record Test(List<String> givenLines, List<String> whenLines, List<String> thenLines) {
}
