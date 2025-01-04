package com.lolplane.fudge.generation;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lolplane.fudge.generation.dto.DataTable;
import com.lolplane.fudge.generation.dto.Line;
import com.lolplane.fudge.generation.dto.LineElement;
import com.lolplane.fudge.jgiven.TestIOUtils;
import com.lolplane.fudge.jgiven.dto.JGivenDataTable;
import com.lolplane.fudge.jgiven.dto.JGivenKeyword;
import com.lolplane.fudge.jgiven.dto.JGivenKeywordArgumentInfo;
import com.lolplane.fudge.jgiven.dto.JGivenScenarioCase;
import com.lolplane.fudge.jgiven.dto.JGivenStep;
import com.lolplane.fudge.jgiven.dto.JGivenTestClass;
import lombok.SneakyThrows;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class WordsAnalyzerTest {

    @Test
    @DisplayName("should analyze gherkin structure")
    void shouldAnalyzeGherkinStructure() {
        var scenarioCase = new JGivenScenarioCase(1, List.of(
            new JGivenStep("an anonymous user", List.of(
                new JGivenKeyword("Given", true, null),
                new JGivenKeyword("an anonymous user", false, null)
            ), "PASSED", 2325666, 0, false),
            new JGivenStep("a configured site $ on $", List.of(
                new JGivenKeyword("and", true, null),
                new JGivenKeyword("a configured site", false, null),
                new JGivenKeyword("Germany", false, new JGivenKeywordArgumentInfo("siteName", null, "Germany", null)),
                new JGivenKeyword("on", false, null),
                new JGivenKeyword("mWeb", false, null),
                new JGivenKeyword("EventWithAward[name\u003dpurchase, amount\u003d100], EventWithAward[name\u003dsale, amount\u003d50], " +
                    "EventWithAward[name\u003dsharing on social media, amount\u003d10]", false, new JGivenKeywordArgumentInfo("platform", null, "mWeb",
                    JGivenDataTable
                        .builder()
                        .headerType("HORIZONTAL")
                        .withRow("name", "amount")
                        .withRow("purchase", "100")
                        .withRow("sale", "50")
                        .withRow("sharing on social media", "10")
                        .build()))
            ), "PASSED", 2325666, 0, false),
            new JGivenStep("requesting the welcome page", List.of(
                new JGivenKeyword("When", true, null),
                new JGivenKeyword("requesting the welcome page", false, null)
            ), "PASSED", 2325666, 0, false),
            new JGivenStep("the German welcome page is returned", List.of(
                new JGivenKeyword("Then", true, null),
                new JGivenKeyword("the German welcome page is returned", false, null)
            ), "PASSED", 2325666, 0, false)
        ), List.of(), List.of(), "PASSED", 2325666);

        var wordsAnalyzer = WordsAnalyzer.analyze(scenarioCase);

        SoftAssertions.assertSoftly(softly -> {
            softly
                .assertThat(wordsAnalyzer.given())
                .map(Line::value)
                .filteredOn(Objects::nonNull)
                .map(l -> l.stream().map(LineElement::wordGroup).collect(Collectors.joining(" ")))
                .containsExactly("an anonymous user", "and a configured site Germany on mWeb", "");
            softly
                .assertThat(wordsAnalyzer.when())
                .map(Line::value)
                .map(l -> l.stream().map(LineElement::wordGroup).collect(Collectors.joining(" ")))
                .containsExactly("requesting the welcome page");
            softly
                .assertThat(wordsAnalyzer.then())
                .map(Line::value)
                .map(l -> l.stream().map(LineElement::wordGroup).collect(Collectors.joining(" ")))
                .containsExactly("the German welcome page is returned");
            softly.assertThat(wordsAnalyzer.given())
                  .map(Line::table)
                  .last()
                  .isEqualTo(DataTable
                      .builder()
                      .withRow("name", "amount")
                      .withRow("purchase", "100")
                      .withRow("sale", "50")
                      .withRow("sharing on social media", "10")
                      .build());
        });
    }

    @SneakyThrows
    @Test
    @DisplayName("should use parameters in generated lines if derived/explicit arguments were used in the testcase")
    void shouldUseParametersInGeneratedLinesIfDerivedExplicitArgumentsWereUsedInTheTestcase() {
        var sourceText = TestIOUtils.loadTextFile("jgiven-report-with-parameters.json");
        var jGivenTestClass = new ObjectMapper().readValue(sourceText, JGivenTestClass.class);
        var jGivenScenario = jGivenTestClass.scenarios().get(0);

        var actual = WordsAnalyzer.analyze(jGivenScenario.scenarioCases().get(0));

        assertThat(actual.given())
            .flatMap(Line::value)
            .containsExactly(LineElement.wordGroup("an anonymous user"), LineElement.wordGroup("and"), LineElement.wordGroup("an event"),
                LineElement.parameter("name"));
    }
}
