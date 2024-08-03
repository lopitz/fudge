package de.opitz.poc.featuredoc.generation;

import java.util.Iterator;

import de.opitz.poc.featuredoc.generation.dto.Scenario;
import de.opitz.poc.featuredoc.generation.dto.Test;
import de.opitz.poc.featuredoc.jgiven.dto.JGivenScenario;
import de.opitz.poc.featuredoc.jgiven.dto.JGivenScenarioCase;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface ScenarioToTestMapper {

    @Mapping(target = "tests", source = "scenarioCases")
    @Mapping(target = "index", expression = "java(counter.next())")
    Scenario map(JGivenScenario scenario, @Context Iterator<Integer> counter);

    default Test map(JGivenScenarioCase source) {
        var wordsWorking = WordsAnalyzer.analyze(source);
        return new Test(wordsWorking.given(), wordsWorking.when(), wordsWorking.then());
    }

}
