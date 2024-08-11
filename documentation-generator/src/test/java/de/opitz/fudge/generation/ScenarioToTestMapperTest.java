package de.opitz.fudge.generation;

import java.util.List;

import de.opitz.fudge.generation.dto.Case;
import de.opitz.fudge.generation.dto.ConnectedIssue;
import de.opitz.fudge.generation.dto.Line;
import de.opitz.fudge.generation.dto.LineElement;
import de.opitz.fudge.generation.dto.Parameter;
import de.opitz.fudge.jgiven.JGivenJsonParser;
import lombok.SneakyThrows;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static de.opitz.fudge.jgiven.TestIOUtils.fileUrl;

class ScenarioToTestMapperTest {

    @Test
    @DisplayName("should create description with parameterNames and case table")
    @SneakyThrows
    void shouldCreateDescriptionWithParameterNamesAndCaseTable() {
        var jGivenReport = new JGivenJsonParser().parseReportFiles(fileUrl("jgiven-report-with-parameters.json"));
        var jGivenTestClass = jGivenReport.testClasses().get(0);
        var jGivenScenario = jGivenTestClass.scenarios().get(0);

        var actual = Mappers.getMapper(ScenarioToTestMapper.class).map(jGivenScenario, IdGenerator.ofInt(1).iterator());

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(actual.epics()).containsExactly(new ConnectedIssue("FEATUREDOCS-1000", "https://jira.dev/browse/FEATUREDOCS-1000"));
            softly
                .assertThat(actual.stories())
                .containsExactly(new ConnectedIssue("FEATUREDOCS-1123", "https://jira.dev/browse/FEATUREDOCS-1123"), new ConnectedIssue("FEATUREDOCS-1224",
                    "https://jira" +
                        ".dev/browse/FEATUREDOCS-1224"));
            softly.assertThat(actual.tests())
                  .singleElement()
                  .isEqualTo(new de.opitz.fudge.generation.dto.Test(List.of(new Line(List.of(LineElement.wordGroup("an anonymous user"))),
                      new Line(List.of(LineElement.wordGroup("and"), LineElement.wordGroup("an event"), LineElement.parameter("name")))),
                      List.of(new Line(List.of(LineElement.wordGroup("requesting the welcome page")))),
                      List.of(new Line(List.of(LineElement.wordGroup("the award accrued is"), LineElement.parameter("award"))))));
            softly.assertThat(actual.cases()).isNotNull();
            softly.assertThat(actual.cases().parameterNames()).containsExactly("name", "award");
            softly.assertThat(actual.cases().cases()).hasSize(3);
            softly
                .assertThat(actual.cases().cases())
                .first()
                .isEqualTo(new Case(1, List.of(new Parameter("name", "purchase 1"), new Parameter("award", "150"))));
        });
    }
}
