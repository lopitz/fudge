package com.lolplane.fudge.generation.dto;

import java.util.List;
import java.util.Objects;

public record Line(List<LineElement> value, DataTable table) {

    public Line(List<LineElement> value, DataTable table) {
        this.value = List.copyOf(Objects.requireNonNullElse(value, List.of()));
        this.table = table;
    }

    public Line(List<LineElement> value) {
        this(value, null);
    }

}
