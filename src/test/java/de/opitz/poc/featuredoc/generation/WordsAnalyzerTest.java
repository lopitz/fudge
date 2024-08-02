package de.opitz.poc.featuredoc.generation;

import java.util.List;

import de.opitz.poc.featuredoc.jgiven.dto.JGivenKeyword;
import de.opitz.poc.featuredoc.jgiven.dto.JGivenKeywordArgumentInfo;
import de.opitz.poc.featuredoc.jgiven.dto.JGivenScenarioCase;
import de.opitz.poc.featuredoc.jgiven.dto.JGivenStep;
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
                new JGivenKeyword("Germany", false, new JGivenKeywordArgumentInfo("siteName", "Germany")),
                new JGivenKeyword("on", false, null),
                new JGivenKeyword("mWeb", false, new JGivenKeywordArgumentInfo("platform", "mWeb"))
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

        assertThat(wordsAnalyzer.given()).containsExactly("an anonymous user", "and a configured site Germany on mWeb");
        assertThat(wordsAnalyzer.when()).containsExactly("requesting the welcome page");
        assertThat(wordsAnalyzer.then()).containsExactly("the German welcome page is returned");
    }

}
