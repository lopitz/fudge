package com.lolplane.fudge.generators;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Optional;

import com.lolplane.fudge.PrintWriterConsoleWriter;
import com.lolplane.fudge.annotations.Epic;
import com.lolplane.fudge.annotations.Story;
import com.tngtech.jgiven.annotation.TagHrefGenerator;
import com.tngtech.jgiven.config.TagConfiguration;

import static java.util.function.Predicate.not;

public class JiraLinkGenerator implements TagHrefGenerator {

    private static final String JIRA_BASE_URL_VARIABLE = "JIRA_BASE_URL";
    private static final String JIRA_BASE_URL_PROPERTY = "jira.base.url";
    private static final String JIRA_URL = "%s/browse/%s";

    private final Map<String, String> environmentVariables;
    private final PrintWriterConsoleWriter consoleWriter;

    @SuppressWarnings("unused") //called by JGiven
    public JiraLinkGenerator() {
        this(new PrintWriterConsoleWriter(), System.getenv());
    }

    public JiraLinkGenerator(PrintWriterConsoleWriter consoleWriter) {
        this(consoleWriter, System.getenv());
    }

    public JiraLinkGenerator(PrintWriterConsoleWriter consoleWriter, Map<String, String> environmentVariables) {
        this.consoleWriter = consoleWriter;
        this.environmentVariables = environmentVariables;
    }

    @Override
    public String generateHref(TagConfiguration tagConfiguration, Annotation annotation, Object value) {
        if (value instanceof String issue) {
            return extractJiraBaseUrl(annotation)
                .map(this::removeTrailingForwardSlashes)
                .map(baseUrl -> JIRA_URL.formatted(baseUrl, removeBeginningForwardSlashes(removeTrailingForwardSlashes(issue))))
                .orElseGet(this::logMissingJiraConfiguration);
        }
        return "";
    }

    private String logMissingJiraConfiguration() {
        consoleWriter.println("!!! JGiven report generation - information. !!!");
        consoleWriter.println("Neither the system property \"{}\" nor the environment variable \"{}\" was set.", JIRA_BASE_URL_PROPERTY,
            JIRA_BASE_URL_VARIABLE);
        consoleWriter.println("Consider passing url as system property when starting the build. e.g for maven: mvn -D{}=https://jira.your.company.com",
            JIRA_BASE_URL_PROPERTY);
        consoleWriter.println("No Jira links will be generated.");
        consoleWriter.println("!!! JGiven report generation - information end. !!!");
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
        } else if (annotation instanceof Epic epic) {
            return epic.jiraBaseUrl();
        }
        return null;
    }

    private String removeTrailingForwardSlashes(String input) {
        if (input.isEmpty()) {
            return input;
        }

        var lastIndex = input.length() - 1;
        while (lastIndex >= 0 && input.charAt(lastIndex) == '/') {
            lastIndex--;
        }

        return input.substring(0, lastIndex + 1);
    }

    private String removeBeginningForwardSlashes(String input) {
        if (input.isEmpty()) {
            return input;
        }

        var lastIndex = 0;
        while (lastIndex < input.length() && input.charAt(lastIndex) == '/') {
            lastIndex++;
        }

        return input.substring(lastIndex);
    }

}
