package de.opitz.poc.featuredoc.generation.dto;

import java.util.List;

public record Cases(List<String> parameterNames, List<Case> cases) {
}
