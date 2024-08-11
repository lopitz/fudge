package de.opitz.fudge.generators;

import java.lang.annotation.Annotation;

import com.tngtech.jgiven.annotation.TagDescriptionGenerator;
import com.tngtech.jgiven.config.TagConfiguration;

public class DescriptionFromValueGenerator implements TagDescriptionGenerator {

    @Override
    public String generateDescription(TagConfiguration tagConfiguration, Annotation annotation, Object value) {
        if (value instanceof String issue) {
            return issue;
        }
        return "";
    }
}
