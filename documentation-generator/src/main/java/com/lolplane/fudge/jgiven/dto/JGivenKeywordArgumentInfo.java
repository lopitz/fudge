package com.lolplane.fudge.jgiven.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record JGivenKeywordArgumentInfo(String argumentName, String parameterName, String formattedValue, JGivenDataTable dataTable) {
}
