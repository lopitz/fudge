package de.opitz.poc.featuredoc.generation;

import java.util.List;

import de.opitz.poc.featuredoc.generation.dto.Case;
import de.opitz.poc.featuredoc.generation.dto.ConnectedIssue;
import de.opitz.poc.featuredoc.generation.dto.Line;
import de.opitz.poc.featuredoc.generation.dto.Parameter;
import de.opitz.poc.featuredoc.jgiven.JGivenJsonParser;
import lombok.SneakyThrows;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static de.opitz.poc.featuredoc.generation.dto.LineElement.parameter;
import static de.opitz.poc.featuredoc.generation.dto.LineElement.wordGroup;
import static de.opitz.poc.featuredoc.jgiven.TestIOUtils.fileUrl;

class ScenarioToTestMapperTest {

    @Test
    @DisplayName("should create description with parameterNames and case table")
    @SneakyThrows
    void shouldCreateDescriptionWithParameterNamesAndCaseTable() {
        var jGivenReport = new JGivenJsonParser().parseReportFiles(fileUrl("jgiven-report-with-parameters.json"));
        var jGivenTestClass = jGivenReport.testClasses().getFirst();
        var jGivenScenario = jGivenTestClass.scenarios().getFirst();

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
                  .isEqualTo(new de.opitz.poc.featuredoc.generation.dto.Test(List.of(new Line(List.of(wordGroup("an anonymous user"))),
                      new Line(List.of(wordGroup("and"), wordGroup("an event"), parameter("name")))),
                      List.of(new Line(List.of(wordGroup("requesting the welcome page")))),
                      List.of(new Line(List.of(wordGroup("the award accrued is"), parameter("award"))))));
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
