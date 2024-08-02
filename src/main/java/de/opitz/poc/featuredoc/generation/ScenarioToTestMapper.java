package de.opitz.poc.featuredoc.generation;

import de.opitz.poc.featuredoc.generation.dto.Scenario;
import de.opitz.poc.featuredoc.generation.dto.Test;
import de.opitz.poc.featuredoc.jgiven.dto.JGivenScenario;
import de.opitz.poc.featuredoc.jgiven.dto.JGivenScenarioCase;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface ScenarioToTestMapper {

    @Mapping(target = "tests", source = "scenarioCases")
    Scenario map(JGivenScenario scenario);

    default Test map(JGivenScenarioCase source) {
        var wordsWorking = WordsAnalyzer.analyze(source);
        return new Test(wordsWorking.given(), wordsWorking.when(), wordsWorking.then());
    }

}
