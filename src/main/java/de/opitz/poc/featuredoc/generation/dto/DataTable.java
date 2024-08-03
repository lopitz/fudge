package de.opitz.poc.featuredoc.generation.dto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public record DataTable(String headerType, List<String> header, List<Row> rows) {

    public static DataTableBuilder builder() {
        return new DataTableBuilder();
    }

    public static class DataTableBuilder {

        private final List<Row> rows = new ArrayList<>();

        private String headerType;
        private int numberOfColumns = 0;

        DataTableBuilder() {
            this.headerType = "HORIZONTAL";
        }

        public DataTableBuilder headerType(String headerType) {
            this.headerType = headerType;
            return this;
        }

        public DataTableBuilder withRow(String... data) {
            if (numberOfColumns == 0) {
                numberOfColumns = data.length;
            }
            if (data.length != numberOfColumns) {
                throw new IllegalArgumentException("The number of columns is inconsistent");
            }
            rows.add(new Row(Arrays.asList(data)));
            return this;
        }

        public DataTable build() {
            return new DataTable(headerType, rows.removeFirst().columns(), rows);
        }

        public DataTableBuilder withRows(List<List<String>> data) {
            data.forEach(row -> rows.add(new Row(row)));
            return this;
        }
    }
}
