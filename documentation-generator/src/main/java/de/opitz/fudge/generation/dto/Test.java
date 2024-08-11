package de.opitz.fudge.generation.dto;

import java.util.List;

public record Test(List<Line> givenLines, List<Line> whenLines, List<Line> thenLines) {
}
