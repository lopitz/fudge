package com.lolplane.fudge.jgiven.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record JGivenStep(
    String name,
    List<JGivenKeyword> words,
    String status,
    long durationInNanos,
    int depth,
    boolean parentFailed
) {
}
