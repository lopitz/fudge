package com.lolplane.fudge.jgiven.dto;

import com.lolplane.fudge.jgiven.JGivenJsonParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.lolplane.fudge.jgiven.TestIOUtils.fileUrl;
import static org.assertj.core.api.Assertions.assertThat;

class JGivenReportTest {

    private JGivenReport report;

    @BeforeEach
    void setupTest() {
        report = new JGivenJsonParser().parseReportFiles(fileUrl("jgiven-report.json"));
    }

    @Test
    @DisplayName("should handle tags properly")
    void shouldHandleTagsProperly() {
        assertThat(report.filterByTag("Story", "FEATUREDOCS-2311"))
            .singleElement()
            .hasFieldOrPropertyWithValue("testMethodName", "providesAWelcomePage");
    }

    @Test
    @DisplayName("should return scenarios of a given tag type")
    void shouldReturnScenariosOfAGivenTagType() {
        assertThat(report.filterByTag("Story")).hasSize(2);
    }

    @Test
    @DisplayName("should return an empty list if no scenario matches given tag")
    void shouldReturnAnEmptyListIfNoScenarioMatchesGivenTag() {
        assertThat(report.filterByTag("non existing tag")).isEmpty();
    }

    @Test
    @DisplayName("should return an empty list if no value matches given tag value")
    void shouldReturnAnEmptyListIfNoValueMatchesGivenTagValue() {
        assertThat(report.filterByTag("Story", "not existing tag value")).isEmpty();

    }

    @Test
    @DisplayName("should return an empty list if no scenario was tagged with given tag")
    void shouldReturnAnEmptyListIfNoScenarioWasTaggedWithGivenTag() {
        assertThat(report.filterByTag("non existing tag", "non existing tag value")).isEmpty();

    }

}
