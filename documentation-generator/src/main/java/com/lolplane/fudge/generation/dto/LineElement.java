package com.lolplane.fudge.generation.dto;

public record LineElement(String wordGroup, String parameter) {

    public static LineElement parameter(String parameter) {
        return new LineElement(null, parameter);
    }

    public static LineElement wordGroup(String word) {
        return new LineElement(word, null);
    }

}
