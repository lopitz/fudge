package com.lolplane.fudge.generators;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DescriptionFromValueGeneratorTest {

    @Test
    @NumberTestAnnotation(2311)
    @DisplayName("should return an empty if the annotation value is not a string")
    void shouldReturnAnEmptyIfTheAnnotationValueIsNotAString() {
        var generator = new DescriptionFromValueGenerator();
        var annotation = AnnotationExtractor.extractAnnotationOfTypeFromAnyMethodInClass(NumberTestAnnotation.class, DescriptionFromValueGeneratorTest.class);
        var expected = annotation.value();
        var actual = generator.generateDescription(null, annotation, expected);
        assertThat(actual).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("should return the value of the annotation in case it is a string")
    void shouldReturnTheValueOfTheAnnotationInCaseItIsAString() {
        var generator = new DescriptionFromValueGenerator();
        var annotation = AnnotationExtractor.extractAnnotationOfTypeFromAnyMethodInClass(DisplayName.class, DescriptionFromValueGeneratorTest.class);
        var expected = annotation.value();
        var actual = generator.generateDescription(null, annotation, expected);
        assertThat(actual).isNotEmpty().isEqualTo(expected);
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface NumberTestAnnotation {
        int value();
    }
}
