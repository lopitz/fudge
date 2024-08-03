package de.opitz.poc.featuredoc.generation.dto;

import java.util.List;

public record Test(List<Line> givenLines, List<Line> whenLines, List<Line> thenLines) {
}
