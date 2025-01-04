package com.lolplane.fudge.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.lolplane.fudge.generators.DescriptionFromValueGenerator;
import com.lolplane.fudge.generators.JiraLinkGenerator;
import com.tngtech.jgiven.annotation.IsTag;

@IsTag(descriptionGenerator = DescriptionFromValueGenerator.class, hrefGenerator = JiraLinkGenerator.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface Story {
    String[] value();

    /**
     * Base URL of the JIRA system. If this is not set, then the system property "JIRA_BASE_URL" and the environment variable "JIRA_BASE_URL" will be evaluated.
     */
    String jiraBaseUrl() default "";
}
