package de.opitz.poc.featuredoc.jgiven;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static de.opitz.poc.featuredoc.jgiven.TestIOUtils.fileUrl;
import static org.assertj.core.api.Assertions.assertThat;

class JGivenJsonParserTest {

    @Test
    @DisplayName("should parse JGiven JSON report properly")
    void shouldParseJGivenJsonReportProperly() {
        var actual = new JGivenJsonParser().parseReportFiles(fileUrl("jgiven-report.json"));

        assertThat(actual).isNotNull();
        assertThat(actual.testClasses().getFirst().scenarios().getFirst().scenarioCases().getFirst().steps().get(1).words())
            .extracting("value")
            .containsExactly("and", "a configured site", "Germany", "on", "mWeb");
    }

}
