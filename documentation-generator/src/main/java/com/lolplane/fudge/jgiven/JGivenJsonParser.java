package com.lolplane.fudge.jgiven;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.lolplane.fudge.jgiven.dto.JGivenReport;
import com.lolplane.fudge.jgiven.dto.JGivenScenario;
import com.lolplane.fudge.jgiven.dto.JGivenTag;
import com.lolplane.fudge.jgiven.dto.JGivenTestClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JGivenJsonParser {

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new Jdk8Module());

    public JGivenReport parseReportFiles(URL... urls) {
        return parseReportFiles(Arrays.stream(urls));
    }

    public JGivenReport parseReportFiles(Stream<URL> urls) {
        return urls
            .parallel()
            .map(this::loadReport)
            .filter(Objects::nonNull)
            .map(this::enrichScenariosWithTags)
            .reduce(JGivenReport.empty(), JGivenReport::withTestClass, JGivenReport::join);
    }

    private JGivenTestClass enrichScenariosWithTags(JGivenTestClass testClass) {
        var enrichedScenarios = testClass
            .scenarios()
            .stream()
            .map(oldScenario -> enrichScenarioWithTags(oldScenario, testClass.tagMap()))
            .toList();
        return testClass.withScenarios(enrichedScenarios);
    }

    private JGivenScenario enrichScenarioWithTags(JGivenScenario oldScenario, Map<String, JGivenTag> tagMap) {
        var newTags = oldScenario.tagIds().stream().map(tagMap::get).toList();
        return oldScenario.withTags(newTags);
    }

    private JGivenTestClass loadReport(URL url) {
        try {
            return objectMapper.readValue(url, JGivenTestClass.class);
        } catch (IOException e) {
            log.error("Error while loading report from {}", url, e);
        }
        return null;
    }
}
