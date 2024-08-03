package de.opitz.poc.featuredoc.generation;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import de.opitz.poc.featuredoc.generation.dto.Case;
import de.opitz.poc.featuredoc.generation.dto.Cases;
import de.opitz.poc.featuredoc.generation.dto.Parameter;
import de.opitz.poc.featuredoc.generation.dto.Scenario;
import de.opitz.poc.featuredoc.generation.dto.Test;
import de.opitz.poc.featuredoc.jgiven.dto.JGivenKeyword;
import de.opitz.poc.featuredoc.jgiven.dto.JGivenScenario;
import de.opitz.poc.featuredoc.jgiven.dto.JGivenScenarioCase;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface ScenarioToTestMapper {

    @Mapping(target = "tests", source = "scenarioCases")
    @Mapping(target = "index", expression = "java(counter.next())")
    default Scenario map(JGivenScenario scenario, @Context Iterator<Integer> counter) {
        if (scenario.casesAsTable()) {
            return new Scenario(counter.next(), scenario.description(), List.of(map(scenario.scenarioCases().getFirst())), mapCases(scenario));
        }
        return new Scenario(counter.next(), scenario.description(), scenario.scenarioCases().stream().map(this::map).toList(), null);
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
