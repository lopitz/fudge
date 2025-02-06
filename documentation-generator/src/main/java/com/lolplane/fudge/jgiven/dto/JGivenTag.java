package com.lolplane.fudge.jgiven.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record JGivenTag(String fullType, String type, String name, String value, String description, String href) {
}
