package com.lolplane.fudge.generators;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Optional;

import com.lolplane.fudge.annotations.Story;
import com.tngtech.jgiven.annotation.TagHrefGenerator;
import com.tngtech.jgiven.config.TagConfiguration;
import lombok.extern.slf4j.Slf4j;

import static java.util.function.Predicate.not;

@Slf4j
public class JiraLinkGenerator implements TagHrefGenerator {

    private static final String JIRA_BASE_URL_VARIABLE = "JIRA_BASE_URL";
    private static final String JIRA_BASE_URL_PROPERTY = "jira.base.url";
    private static final String JIRA_URL = "%s/browse/%s";

    private final Map<String, String> environmentVariables;

    public JiraLinkGenerator() {
        environmentVariables = System.getenv();
    }

    public JiraLinkGenerator(Map<String, String> environmentVariables) {
        this.environmentVariables = environmentVariables;
    }

    @Override
    public String generateHref(TagConfiguration tagConfiguration, Annotation annotation, Object value) {
        if (value instanceof String issue) {
            return extractJiraBaseUrl(annotation)
                .map(this::removeTrailingForwardSlashes)
                .map(baseUrl -> JIRA_URL.formatted(baseUrl, issue))
                .orElseGet(this::logMissingJiraConfiguration);
        }
        return "";
    }

    private String logMissingJiraConfiguration() {
        log.warn("!!! JGiven report generation - information. !!!");
        log.warn("Neither the system property \"{}\" nor the environment variable \"{}\" was set.%n", JIRA_BASE_URL_PROPERTY,
            JIRA_BASE_URL_VARIABLE);
        log.warn("Consider passing url as system property when starting the build. e.g for maven: mvn -D{}=https://jira.your.company.com",
            JIRA_BASE_URL_PROPERTY);
        log.warn("No Jira links will be generated.");
        log.warn("!!! JGiven report generation - information end. !!!");
        return "";
    }

    private Optional<String> extractJiraBaseUrl(Annotation annotation) {
        return extractJiraBaseUrlFromEnvironment()
            .or(() -> Optional.ofNullable(extractJiraBaseUrlFromAnnotation(annotation)))
            .filter(not(String::isEmpty));
    }

    private Optional<String> extractJiraBaseUrlFromEnvironment() {
        return Optional
            .ofNullable(System.getProperty(JIRA_BASE_URL_PROPERTY))
            .filter(not(String::isBlank))
            .or(() -> Optional.ofNullable(environmentVariables.get(JIRA_BASE_URL_VARIABLE)))
            .map(this::removeTrailingForwardSlashes);
    }

    private String extractJiraBaseUrlFromAnnotation(Annotation annotation) {
        if (annotation instanceof Story story) {
            return story.jiraBaseUrl();
        }
        return null;
    }

    private String removeTrailingForwardSlashes(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        var lastIndex = input.length() - 1;
        while (lastIndex >= 0 && input.charAt(lastIndex) == '/') {
            lastIndex--;
        }

        return input.substring(0, lastIndex + 1);
    }

}
