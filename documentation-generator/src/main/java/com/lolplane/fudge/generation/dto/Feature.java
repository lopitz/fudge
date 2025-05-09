package com.lolplane.fudge.generation.dto;

import java.util.List;
import java.util.Objects;

import lombok.With;

@With
public record Feature(
    String name,
    String description,
    String featureFolder,
    List<Scenario> scenarios,
    List<ConnectedIssue> epics,
    List<ConnectedIssue> stories
) {
    public String getEncodedFeatureFolder() {
        return Objects.requireNonNullElse(featureFolder, "").replace(" ", "%20");
    }
}
