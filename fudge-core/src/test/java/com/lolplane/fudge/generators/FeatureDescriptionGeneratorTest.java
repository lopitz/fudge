package com.lolplane.fudge.generators;

import com.lolplane.fudge.annotations.Feature;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FeatureDescriptionGeneratorTest {

    @Test
    @DisplayName("should return an empty string if the annotation given is not a feature annotation")
    void shouldReturnAnEmptyStringIfTheAnnotationGivenIsNotAFeatureAnnotation() {
        var generator = new FeatureDescriptionGenerator();
        var annotation = AnnotationExtractor.extractAnnotationOfTypeFromAnyMethodInClass(DisplayName.class, FeatureDescriptionGeneratorTest.class);
        var expected = annotation.value();
        var actual = generator.generateDescription(null, annotation, expected);
        assertThat(actual).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("should extract feature description from feature annotation")
    @Feature(value = "Feature-2311", description = "What a description!")
    void shouldExtractFeatureDescriptionFromFeatureAnnotation() {
        var generator = new FeatureDescriptionGenerator();
        var annotation = AnnotationExtractor.extractAnnotationOfTypeFromAnyMethodInClass(Feature.class, FeatureDescriptionGeneratorTest.class);
        var expected = annotation.description();
        var actual = generator.generateDescription(null, annotation, expected);
        assertThat(actual).isNotEmpty().isEqualTo(expected);
    }

}
