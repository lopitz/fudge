package de.opitz.poc.featuredoc.jgiven;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.tngtech.jgiven.annotation.IsTag;

@IsTag(descriptionGenerator = JiraLinkGenerator.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface Story {
    String[] value();
}
