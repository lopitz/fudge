package com.lolplane.fudge.jgiven.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record JGivenKeyword(String value, @JsonProperty("isIntroWord") boolean introWord, JGivenKeywordArgumentInfo argumentInfo) {
}
