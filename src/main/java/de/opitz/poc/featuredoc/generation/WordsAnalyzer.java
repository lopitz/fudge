package de.opitz.poc.featuredoc.generation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import de.opitz.poc.featuredoc.generation.dto.DataTable;
import de.opitz.poc.featuredoc.generation.dto.Line;
import de.opitz.poc.featuredoc.generation.dto.LineElement;
import de.opitz.poc.featuredoc.jgiven.dto.JGivenDataTable;
import de.opitz.poc.featuredoc.jgiven.dto.JGivenKeyword;
import de.opitz.poc.featuredoc.jgiven.dto.JGivenKeywordArgumentInfo;
import de.opitz.poc.featuredoc.jgiven.dto.JGivenScenarioCase;
import de.opitz.poc.featuredoc.jgiven.dto.JGivenStep;

import static de.opitz.poc.featuredoc.generation.dto.LineElement.parameter;
import static de.opitz.poc.featuredoc.generation.dto.LineElement.wordGroup;

public class WordsAnalyzer {
    private final List<Line> given = new ArrayList<>();
    private final List<Line> when = new ArrayList<>();
    private final List<Line> then = new ArrayList<>();
    private final List<LineElement> currentLine = new ArrayList<>();

    private List<Line> currentWordList = given;
    private boolean skipCurrentWord = false;

    private void addWord(JGivenKeyword keyword) {
        skipCurrentWord = false;
        determineCurrentGherkinWordList(keyword);
        if (!skipCurrentWord) {
            addCurrentWord(keyword);
        }
    }

    private void addCurrentWord(JGivenKeyword keyword) {
         Optional
            .of(keyword)
            .map(JGivenKeyword::argumentInfo)
            .map(JGivenKeywordArgumentInfo::dataTable)
            .ifPresentOrElse(this::addTable, () -> addKeyword(keyword));
    }

    private void addKeyword(JGivenKeyword keyword) {
        Optional
            .of(keyword)
            .map(JGivenKeyword::argumentInfo)
            .map(JGivenKeywordArgumentInfo::parameterName)
            .ifPresentOrElse(
                parameterName -> currentLine.add(parameter(parameterName)),
                () -> currentLine.add(wordGroup(keyword.value()))
            );
    }

    private void addTable(JGivenDataTable table) {
        finishLine();
        currentWordList.add(buildTable(table));
    }

    private Line buildTable(JGivenDataTable table) {
        return new Line(null, map(table.data()));
    }

    private DataTable map(List<List<String>> data) {
        return DataTable.builder().withRows(data).build();
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
        if (currentLine.isEmpty()) {
            return;
        }
        currentWordList.add(new Line(currentLine, null));
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

    public List<Line> given() {
        return given;
    }

    public List<Line> when() {
        return when;
    }

    public List<Line> then() {
        return then;
    }
}
