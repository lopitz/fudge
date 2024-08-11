package de.opitz.fudge.generation;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;

import de.opitz.fudge.generation.dto.Case;
import de.opitz.fudge.generation.dto.Cases;
import de.opitz.fudge.generation.dto.ConnectedIssue;
import de.opitz.fudge.generation.dto.Parameter;
import de.opitz.fudge.generation.dto.Scenario;
import de.opitz.fudge.generation.dto.Test;
import de.opitz.fudge.jgiven.dto.JGivenKeyword;
import de.opitz.fudge.jgiven.dto.JGivenScenario;
import de.opitz.fudge.jgiven.dto.JGivenScenarioCase;
import de.opitz.fudge.jgiven.dto.JGivenTag;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface ScenarioToTestMapper {

    @Mapping(target = "tests", source = "scenarioCases")
    @Mapping(target = "index", expression = "java(counter.next())")
    default Scenario map(JGivenScenario scenario, @Context Iterator<Integer> counter) {
        var epics = findEpics(scenario);
        var stories = findStories(scenario);
        if (scenario.casesAsTable()) {
            return new Scenario(
                counter.next(),
                scenario.description(),
                List.of(map(scenario.scenarioCases().get(0))),
                mapCases(scenario),
                epics,
                stories);
        }
        return new Scenario(counter.next(), scenario.description(), scenario.scenarioCases().stream().map(this::map).toList(), null, epics, stories);
    }

    default List<ConnectedIssue> findEpics(JGivenScenario scenario) {
        return getUniqueTagValuesSorted(scenario, jGivenTag -> Objects.equals(jGivenTag.type(), "Epic"));
    }

    default List<ConnectedIssue> findStories(JGivenScenario scenario) {
        return getUniqueTagValuesSorted(scenario, jGivenTag -> Objects.equals(jGivenTag.type(), "Story"));
    }

    private static List<ConnectedIssue> getUniqueTagValuesSorted(JGivenScenario scenario, Predicate<JGivenTag> condition) {
        return Objects
            .requireNonNullElse(scenario.tags(), List.<JGivenTag>of())
            .stream()
            .filter(condition)
            .map(tag -> new ConnectedIssue(tag.value(), tag.href()))
            .distinct()
            .sorted(Comparator.comparing(ConnectedIssue::id))
            .toList();
    }

    default Test map(JGivenScenarioCase source) {
        var wordsWorking = WordsAnalyzer.analyze(source);
        return new Test(wordsWorking.given(), wordsWorking.when(), wordsWorking.then());
    }

    default Cases mapCases(JGivenScenario source) {
        var parameterNames = Stream.concat(source.derivedParameters().stream(), source.explicitParameters().stream()).distinct().toList();
        var cases = source.scenarioCases().stream().map(this::mapToCase).toList();
        return new Cases(parameterNames, cases);
    }

    default Case mapToCase(JGivenScenarioCase jGivenScenarioCase) {
        var parameters = jGivenScenarioCase
            .steps()
            .stream()
            .filter(step -> Objects.nonNull(step.words()) && !step.words().isEmpty())
            .flatMap(step -> step.words().stream())
            .map(JGivenKeyword::argumentInfo)
            .filter(Objects::nonNull)
            .filter(argumentInfo -> Objects.nonNull(argumentInfo.parameterName()))
            .map(argumentInfo -> new Parameter(argumentInfo.parameterName(), argumentInfo.formattedValue()))
            .toList();
        return new Case(jGivenScenarioCase.caseNumber(), parameters);
    }

}
