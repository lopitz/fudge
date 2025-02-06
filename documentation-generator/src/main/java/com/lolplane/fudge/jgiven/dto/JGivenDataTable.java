package com.lolplane.fudge.jgiven.dto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record JGivenDataTable(String headerType, List<List<String>> data) {

    public static JGivenDataTableBuilder builder() {
        return new JGivenDataTableBuilder();
    }

    public static class JGivenDataTableBuilder {

        private final List<List<String>> rows = new ArrayList<>();

        private String headerType;
        private int numberOfColumns = 0;

        JGivenDataTableBuilder() {
            this.headerType = "HORIZONTAL";
        }

        public JGivenDataTableBuilder headerType(String headerType) {
            this.headerType = headerType;
            return this;
        }

        public JGivenDataTableBuilder withRow(String... data) {
            if (numberOfColumns == 0) {
                numberOfColumns = data.length;
            }
            if (data.length != numberOfColumns) {
                throw new IllegalArgumentException("The number of columns is inconsistent");
            }
            rows.add(Arrays.asList(data));
            return this;
        }

        public JGivenDataTable build() {
            return new JGivenDataTable(headerType, rows);
        }
    }
}
