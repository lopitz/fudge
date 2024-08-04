package de.opitz.poc.featuredoc.generation.dto;

import java.util.List;

import lombok.With;

@With
public record Scenario(
    int index,
    String description,
    List<Test> tests,
    Cases cases,
    List<ConnectedIssue> epics,
    List<ConnectedIssue> stories,
    String fileName
) {

    public Scenario(int index, String description, List<Test> tests, Cases cases, List<ConnectedIssue> epics, List<ConnectedIssue> stories) {
        this(index, description, tests, cases, epics, stories, null);
    }
}
