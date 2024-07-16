package de.opitz.poc.featuredoc.jgiven.dto;

import java.util.List;

public record JGivenStep(
    String name,
    List<JGivenKeyword> words,
    String status,
    long durationInNanos,
    int depth,
    boolean parentFailed
) {
}
