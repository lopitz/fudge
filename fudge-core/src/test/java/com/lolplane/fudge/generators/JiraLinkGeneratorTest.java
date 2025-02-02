package com.lolplane.fudge.generators;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

import com.lolplane.fudge.ConsoleWriter;
import com.lolplane.fudge.annotations.Story;
import com.lolplane.fudge.tools.LineBuffer;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class JiraLinkGeneratorTest {

    private final LineBuffer capturedOutput = new LineBuffer();
    private final ConsoleWriter consoleWriter = new ConsoleWriter(capturedOutput.printWriter());

    @Test
    @DisplayName("should generate jira link with given jira base url in story annotation")
    void shouldGenerateJiraLinkWithGivenJiraBaseUrlInStoryAnnotation() {
        System.clearProperty("jira.base.url");
        var jiraLinkGenerator = new JiraLinkGenerator(consoleWriter);
        var actual = jiraLinkGenerator.generateHref(null, getStoryAnnotationWithBaseUrl(), "JIRA-123");

        assertThat(actual).isEqualTo("https://jira.dev/browse/JIRA-123");
    }

    @Test
    @DisplayName("should generate jira link with given jira base url in environment variable")
    void shouldGenerateJiraLinkWithGivenJiraBaseUrlInEnvironmentVariable() {
        System.clearProperty("jira.base.url");
        var jiraLinkGenerator = new JiraLinkGenerator(consoleWriter, Map.of("JIRA_BASE_URL", "https://jira.dev"));
        var actual = jiraLinkGenerator.generateHref(null, getStoryAnnotationWithoutBaseUrl(), "JIRA-123");

        assertThat(actual).isEqualTo("https://jira.dev/browse/JIRA-123");
    }

    @Test
    @DisplayName("should generate jira link if jira base url is given in system property")
    void shouldGenerateJiraLinkIfJiraBaseUrlIsGivenInSystemProperty() {
        try {
            System.setProperty("jira.base.url", "https://jira.dev/");
            var jiraLinkGenerator = new JiraLinkGenerator(consoleWriter);
            var actual = jiraLinkGenerator.generateHref(null, getStoryAnnotationWithoutBaseUrl(), "JIRA-123");

            assertThat(actual).isEqualTo("https://jira.dev/browse/JIRA-123");
        } finally {
            System.clearProperty("jira.base.url");
        }
    }

    @Test
    @DisplayName("should handle not given jira base url gracefully and simply not creating a link")
    void shouldHandleNotGivenJiraBaseUrlGracefullyAndSimplyNotCreatingALink() {
        var oldValue = Optional.ofNullable(System.getProperty("jira.base.url"));
        try {
            System.clearProperty("jira.base.url");
            var jiraLinkGenerator = new JiraLinkGenerator(consoleWriter);
            var actual = jiraLinkGenerator.generateHref(null, getStoryAnnotationWithoutBaseUrl(), "JIRA-123");

            assertThat(actual).isEmpty();
        } finally {
            oldValue.ifPresentOrElse(
                s -> System.setProperty("jira.base.url", s),
                () -> System.clearProperty("jira.base.url"));
        }
    }

    @SneakyThrows
    @Test
    @DisplayName("should log warning message if Jira url is neither given in system props/environment nor provided via annotation")
    void shouldLogWarningMessageIfJiraUrlIsNeitherGivenInSystemPropsEnvironmentNorProvidedViaAnnotation() {
        var oldValue = Optional.ofNullable(System.getProperty("jira.base.url"));
        try {
            System.clearProperty("jira.base.url");
            var jiraLinkGenerator = new JiraLinkGenerator(consoleWriter);
            jiraLinkGenerator.generateHref(null, getStoryAnnotationWithoutBaseUrl(), "JIRA-123");

            assertThat(capturedOutput.lines()).containsExactly(
                "!!! JGiven report generation - information. !!!",
                "Neither the system property \"jira.base.url\" nor the environment variable \"JIRA_BASE_URL\" was set.",
                "Consider passing url as system property when starting the build. e.g for maven: mvn -Djira.base.url=https://jira.your.company.com",
                "No Jira links will be generated.",
                "!!! JGiven report generation - information end. !!!"
            );
        } finally {
            oldValue.ifPresentOrElse(
                s -> System.setProperty("jira.base.url", s),
                () -> System.clearProperty("jira.base.url"));
        }
    }

    @Test
    @DisplayName("should generate jira link when environment and annotation base URLs are missing, and system property is set")
    void shouldGenerateJiraLinkWhenEnvironmentAndAnnotationBaseUrlsAreMissingAndSystemPropertyIsSet() {
        try {
            System.setProperty("jira.base.url", "https://jira.env/");
            var jiraLinkGenerator = new JiraLinkGenerator(consoleWriter);
            var actual = jiraLinkGenerator.generateHref(null, getStoryAnnotationWithoutBaseUrl(), "ISSUE-456");

            assertThat(actual).isEqualTo("https://jira.env/browse/ISSUE-456");
        } finally {
            System.clearProperty("jira.base.url");
        }
    }

    @Test
    @DisplayName("should prefer system property over annotation base URL when generating jira link")
    void shouldPreferSystemPropertyOverAnnotationBaseUrlWhenGeneratingJiraLink() {
        try {
            System.setProperty("jira.base.url", "https://jira.system/");
            var jiraLinkGenerator = new JiraLinkGenerator(consoleWriter);
            var actual = jiraLinkGenerator.generateHref(null, getStoryAnnotationWithBaseUrl(), "ISSUE-789");

            assertThat(actual).isEqualTo("https://jira.system/browse/ISSUE-789");
        } finally {
            System.clearProperty("jira.base.url");
        }
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
        should handle invalid or malformed base URLs gracefully, INVALID-URL, https://jira.malformed/browse/INVALID-URL,
        should remove trailing slashes, no-trailing-slash/, https://jira.malformed/browse/no-trailing-slash,
        should remove double forward slashes, /no-extra-forward-slashes, https://jira.malformed/browse/no-extra-forward-slashes,
        should handle single slash in issue gracefully, /, https://jira.malformed/browse/,
        should handle empty value in issue gracefully, '', https://jira.malformed/browse/
        """)
    void shouldHandleInvalidInputGracefully(@SuppressWarnings("unused") String testName, String issueString, String expected) {
        var jiraLinkGenerator = new JiraLinkGenerator(consoleWriter, Map.of("JIRA_BASE_URL", "https://jira.malformed///"));
        var actual = jiraLinkGenerator.generateHref(null, getStoryAnnotationWithoutBaseUrl(), issueString);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("should handle incompatible annotation gracefully")
    void shouldHandleIncompatibleAnnotationGracefully() {
        System.clearProperty("jira.base.url");
        var jiraLinkGenerator = new JiraLinkGenerator(consoleWriter, Map.of());
        var actual = jiraLinkGenerator.generateHref(null, getIncompatibleAnnotation(), "/");

        assertThat(actual).isEmpty();
    }

    @Test
    @DisplayName("should return empty string when the value passed is not a string")
    void shouldReturnEmptyStringWhenTheValuePassedIsNotAString() {
        var jiraLinkGenerator = new JiraLinkGenerator(consoleWriter);
        var actual = jiraLinkGenerator.generateHref(null, getStoryAnnotationWithoutBaseUrl(), 12345);

        assertThat(actual).isBlank();
    }

    @Test
    @DisplayName("should prefer system property over story annotation and system variable for jira base url")
    void shouldPreferSystemPropertyOverStoryAnnotationAndSystemVariableForJiraBaseUrl() {
        try {
            System.setProperty("jira.base.url", "https://jira.correct/");
            var jiraLinkGenerator = new JiraLinkGenerator(consoleWriter, Map.of("JIRA_BASE_URL", "https://jira.wrong"));
            var actual = jiraLinkGenerator.generateHref(null, getStoryAnnotationWithBaseUrl(), "JIRA-123");

            assertThat(actual).isEqualTo("https://jira.correct/browse/JIRA-123");
        } finally {
            System.clearProperty("jira.base.url");
        }
    }

    @SneakyThrows
    private Annotation getStoryAnnotationWithoutBaseUrl() {
        var method = JiraLinkGeneratorTest.class.getMethod("storyAnnotationWithoutBaseUrl");
        return method.getAnnotation(Story.class);
    }

    @SneakyThrows
    private Annotation getStoryAnnotationWithBaseUrl() {
        var method = JiraLinkGeneratorTest.class.getMethod("storyAnnotationWithBaseUrl");
        return method.getAnnotation(Story.class);
    }

    private Annotation getIncompatibleAnnotation() {
        return Arrays
            .stream(JiraLinkGeneratorTest.class.getDeclaredMethods())
            .map(Method::getAnnotations)
            .flatMap(Arrays::stream)
            .filter(a -> Test.class.isAssignableFrom(a.getClass()))
            .findFirst()
            .orElseThrow();
    }

    @Story(value = "JIRA-123")
    public void storyAnnotationWithoutBaseUrl() {
        // just need the annotation
    }

    @Story(value = "JIRA-123", jiraBaseUrl = "https://jira.dev")
    public void storyAnnotationWithBaseUrl() {
        // just need the annotation
    }

}
