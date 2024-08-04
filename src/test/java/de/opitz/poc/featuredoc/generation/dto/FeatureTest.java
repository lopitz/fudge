package de.opitz.poc.featuredoc.generation.dto;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FeatureTest {

    @Test
    @DisplayName("should encode space in folder name")
    void shouldEncodeSpaceInFolderName() {
        var feature = new Feature("name", "description", "feature folder", List.of(), List.of(), List.of());
        var actual = feature.getEncodedFeatureFolder();
        assertThat(actual).isEqualTo("feature%20folder");
    }

}
