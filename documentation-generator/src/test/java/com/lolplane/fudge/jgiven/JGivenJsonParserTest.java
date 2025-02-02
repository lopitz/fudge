package com.lolplane.fudge.jgiven;

import com.lolplane.fudge.ConsoleWriter;
import com.lolplane.fudge.jgiven.dto.JGivenScenario;
import com.lolplane.fudge.jgiven.dto.JGivenTag;
import com.lolplane.fudge.jgiven.dto.JGivenTestClass;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JGivenJsonParserTest {

    @Test
    @DisplayName("should parse JGiven JSON report properly")
    void shouldParseJGivenJsonReportProperly() {
        var actual = new JGivenJsonParser(new ConsoleWriter()).parseReportFiles(TestIOUtils.fileUrl("jgiven-report.json"));

        assertThat(actual).isNotNull();
        assertThat(actual.testClasses().get(0).scenarios().get(0).scenarioCases().get(0).steps().get(1).words())
            .extracting("value")
            .containsExactly("and", "a configured site", "Germany", "on", "mWeb");
        assertThat(actual.testClasses())
            .flatMap(JGivenTestClass::scenarios)
            .filteredOn("testMethodName", "providesAWelcomePage")
            .flatMap(JGivenScenario::tags)
            .map(JGivenTag::type)
            .containsExactly("Feature", "Story", "Story");
    }

}
