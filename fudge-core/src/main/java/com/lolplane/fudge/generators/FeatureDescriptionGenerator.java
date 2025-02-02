package com.lolplane.fudge.generators;

import java.lang.annotation.Annotation;

import com.lolplane.fudge.annotations.Feature;
import com.tngtech.jgiven.annotation.TagDescriptionGenerator;
import com.tngtech.jgiven.config.TagConfiguration;

public class FeatureDescriptionGenerator implements TagDescriptionGenerator {
    @Override
    public String generateDescription(TagConfiguration tagConfiguration, Annotation annotation, Object value) {
        if (annotation instanceof Feature feature) {
            return feature.description();
        }
        return "";
    }
}
