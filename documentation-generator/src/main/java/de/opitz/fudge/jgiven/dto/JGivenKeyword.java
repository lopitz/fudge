package de.opitz.fudge.jgiven.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record JGivenKeyword(String value, @JsonProperty("isIntroWord") boolean introWord, JGivenKeywordArgumentInfo argumentInfo) {
}
