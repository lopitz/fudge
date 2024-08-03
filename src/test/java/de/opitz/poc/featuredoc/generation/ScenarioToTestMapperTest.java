package de.opitz.poc.featuredoc.generation;

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.opitz.poc.featuredoc.generation.dto.Case;
import de.opitz.poc.featuredoc.generation.dto.Line;
import de.opitz.poc.featuredoc.generation.dto.Parameter;
import de.opitz.poc.featuredoc.jgiven.TestIOUtils;
import de.opitz.poc.featuredoc.jgiven.dto.JGivenTestClass;
import lombok.SneakyThrows;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static de.opitz.poc.featuredoc.generation.dto.LineElement.parameter;
import static de.opitz.poc.featuredoc.generation.dto.LineElement.wordGroup;

class ScenarioToTestMapperTest {

    @Test
    @DisplayName("should create description with parameterNames and case table")
    @SneakyThrows
    void shouldCreateDescriptionWithParameterNamesAndCaseTable() {
        var sourceText = TestIOUtils.loadTextFile("jgiven-report-with-parameters.json");
        var jGivenTestClass = new ObjectMapper().readValue(sourceText, JGivenTestClass.class);
        var jGivenScenario = jGivenTestClass.scenarios().getFirst();

        var actual = Mappers.getMapper(ScenarioToTestMapper.class).map(jGivenScenario, IdGenerator.ofInt(1).iterator());

        SoftAssertions.assertSoftly(softly -> {
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
