package com.lolplane.fudge.generation;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class PathNameCreatorTest {

    private PathNameCreator pathNameCreator;

    @BeforeEach
    void setupTest() {
        pathNameCreator = new PathNameCreator();
    }

    @ParameterizedTest
    @CsvSource({
        "bla/blubb, bla_blubb",
        "bla\\blubb, bla_blubb",
        "bla'blubb, bla_blubb",
        "bla\"blubb, bla_blubb",
        "bla?blubb, bla_blubb",
        "Upper case, upper_case"
    })
    @DisplayName("should replace special characters")
    void shouldReplaceSpecialCharacters(String folderName, String expected) {
        var actual = pathNameCreator.createPathName(folderName);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("should limit folder name to 250 characters")
    void shouldLimitFolderNameTo250Characters() {
        var folderName = RandomStringUtils.randomAlphabetic(300);
        var actual = pathNameCreator.createPathName(folderName);
        assertThat(actual).hasSize(250);
    }


}
