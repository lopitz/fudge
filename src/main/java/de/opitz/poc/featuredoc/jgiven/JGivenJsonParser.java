package de.opitz.poc.featuredoc.jgiven;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Objects;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import de.opitz.poc.featuredoc.jgiven.dto.JGivenReport;
import de.opitz.poc.featuredoc.jgiven.dto.JGivenTestClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JGivenJsonParser {

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new Jdk8Module());

    public JGivenReport parseReportFiles(URL... urls) {
        return Arrays
            .stream(urls)
            .map(this::loadReport)
            .filter(Objects::nonNull)
            .reduce(JGivenReport.empty(), JGivenReport::withTestClass, JGivenReport::join);
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
