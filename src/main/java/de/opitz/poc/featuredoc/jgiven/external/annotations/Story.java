package de.opitz.poc.featuredoc.jgiven.external.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.tngtech.jgiven.annotation.IsTag;
import de.opitz.poc.featuredoc.jgiven.external.generators.DescriptionFromValueGenerator;
import de.opitz.poc.featuredoc.jgiven.external.generators.JiraLinkGenerator;

@IsTag(descriptionGenerator = DescriptionFromValueGenerator.class, hrefGenerator = JiraLinkGenerator.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface Story {
    String[] value();

    /**
     * Base URL of the JIRA system. If this is not set, then the system property "JIRA_BASE_URL" and the environment variable "JIRA_BASE_URL" will be evaluated.
     */
    String jiraBaseUrl() default "";
}
