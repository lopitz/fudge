package de.opitz.fudge.generation.dto;

import java.util.List;

public record Cases(List<String> parameterNames, List<Case> cases) {
}
