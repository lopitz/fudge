package de.opitz.fudge.generators;

import java.lang.annotation.Annotation;
import java.util.Map;

import de.opitz.fudge.annotations.Story;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JiraLinkGeneratorTest {

    @Test
    @DisplayName("should generate jira link with given jira base url in story annotation")
    void shouldGenerateJiraLinkWithGivenJiraBaseUrlInStoryAnnotation() {
        System.clearProperty("jira.base.url");
        var jiraLinkGenerator = new JiraLinkGenerator();
        var actual = jiraLinkGenerator.generateHref(null, getStoryAnnotationWithBaseUrl(), "JIRA-123");

        assertThat(actual).isEqualTo("https://jira.dev/browse/JIRA-123");
    }

    @Test
    @DisplayName("should generate jira link with given jira base url in environment variable")
    void shouldGenerateJiraLinkWithGivenJiraBaseUrlInEnvironmentVariable() {
        System.clearProperty("jira.base.url");
        var jiraLinkGenerator = new JiraLinkGenerator(Map.of("JIRA_BASE_URL", "https://jira.dev"));
        var actual = jiraLinkGenerator.generateHref(null, getStoryAnnotationWithoutBaseUrl(), "JIRA-123");

        assertThat(actual).isEqualTo("https://jira.dev/browse/JIRA-123");
    }

    @Test
    @DisplayName("should generate jira link if jira base url is given in system property")
    void shouldGenerateJiraLinkIfJiraBaseUrlIsGivenInSystemProperty() {
        try {
            System.setProperty("jira.base.url", "https://jira.dev/");
            var jiraLinkGenerator = new JiraLinkGenerator();
            var actual = jiraLinkGenerator.generateHref(null, getStoryAnnotationWithoutBaseUrl(), "JIRA-123");

            assertThat(actual).isEqualTo("https://jira.dev/browse/JIRA-123");
        } finally {
            System.clearProperty("jira.base.url");
        }
    }

    @Test
    @DisplayName("should handle not given jira base url gracefully and simply not creating a link")
    void shouldHandleNotGivenJiraBaseUrlGracefullyAndSimplyNotCreatingALink() {
        System.clearProperty("jira.base.url");
        var jiraLinkGenerator = new JiraLinkGenerator();
        var actual = jiraLinkGenerator.generateHref(null, getStoryAnnotationWithoutBaseUrl(), "JIRA-123");

        assertThat(actual).isEmpty();
    }

    @Test
    @DisplayName("should prefer system property over story annotation and system variable for jira base url")
    void shouldPreferSystemPropertyOverStoryAnnotationAndSystemVariableForJiraBaseUrl() {
        try {
            System.setProperty("jira.base.url", "https://jira.correct/");
            var jiraLinkGenerator = new JiraLinkGenerator(Map.of("JIRA_BASE_URL", "https://jira.wrong"));
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

    @Story(value = "JIRA-123")
    public void storyAnnotationWithoutBaseUrl() {
        // just need the annotation
    }

    @Story(value = "JIRA-123", jiraBaseUrl = "https://jira.dev")
    public void storyAnnotationWithBaseUrl() {
        // just need the annotation
    }

}
