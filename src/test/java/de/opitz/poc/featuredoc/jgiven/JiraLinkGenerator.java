package de.opitz.poc.featuredoc.jgiven;

import java.lang.annotation.Annotation;

import com.tngtech.jgiven.annotation.TagDescriptionGenerator;
import com.tngtech.jgiven.config.TagConfiguration;

public class JiraLinkGenerator implements TagDescriptionGenerator {

    private static final String JIRA_BASE_URL = "https://jira.corp.ebay.com/browse";

    @Override
    public String generateDescription(TagConfiguration tagConfiguration, Annotation annotation, Object value) {
        if (value instanceof String issue) {
            var result = "<a href=\"%s/%2$s>%2$s</a>".formatted(JIRA_BASE_URL, issue);
            System.out.println(result);
            return result;
        }
        return "";
    }
}
