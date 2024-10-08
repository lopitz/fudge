package de.opitz.fudge.generators;

import java.lang.annotation.Annotation;

import com.tngtech.jgiven.annotation.TagDescriptionGenerator;
import com.tngtech.jgiven.config.TagConfiguration;
import de.opitz.fudge.annotations.Feature;

public class FeatureDescriptionGenerator implements TagDescriptionGenerator {
    @Override
    public String generateDescription(TagConfiguration tagConfiguration, Annotation annotation, Object value) {
        if (annotation instanceof Feature feature) {
            return feature.description();
        }
        return "";
    }
}
