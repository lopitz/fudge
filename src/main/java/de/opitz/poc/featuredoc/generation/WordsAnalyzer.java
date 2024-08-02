package de.opitz.poc.featuredoc.generation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.opitz.poc.featuredoc.jgiven.dto.JGivenKeyword;
import de.opitz.poc.featuredoc.jgiven.dto.JGivenScenarioCase;
import de.opitz.poc.featuredoc.jgiven.dto.JGivenStep;

public class WordsAnalyzer {
    private final List<String> given = new ArrayList<>();
    private final List<String> when = new ArrayList<>();
    private final List<String> then = new ArrayList<>();
    private final List<String> currentLine = new ArrayList<>();

    private List<String> currentWordList = given;
    private boolean skipCurrentWord = false;

    private void addWord(JGivenKeyword keyword) {
        skipCurrentWord = false;
        determineCurrentGherkinWordList(keyword);
        if (!skipCurrentWord) {
            currentLine.add(keyword.value());
        }
    }

    private void determineCurrentGherkinWordList(JGivenKeyword keyword) {
        if (keyword.isIntroWord()) {
            finishLine();
            currentWordList = switch (keyword.value()) {
                case "Given" -> {
                    skipCurrentWord();
                    yield given;
                }
                case "When" -> {
                    skipCurrentWord();
                    yield when;
                }
                case "Then" -> {
                    skipCurrentWord();
                    yield then;
                }
                default -> currentWordList;
            };
        }
    }

    private void skipCurrentWord() {
        skipCurrentWord = true;
    }

    private void finishLine() {
        if (!currentLine.isEmpty()) {
            currentWordList.add(String.join(" ", currentLine));
        }
        currentLine.clear();
    }

    private void finish() {
        finishLine();
    }

    public static WordsAnalyzer analyze(JGivenScenarioCase source) {
        var result = new WordsAnalyzer();
        source.steps().stream().map(JGivenStep::words).flatMap(Collection::stream).forEach(result::addWord);

        result.finish();
        return result;
    }

    public List<String> given() {
        return given;
    }

    public List<String> when() {
        return when;
    }

    public List<String> then() {
        return then;
    }
}
