package com.lolplane.fudge.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.lolplane.fudge.generators.FeatureDescriptionGenerator;
import com.tngtech.jgiven.annotation.IsTag;

@IsTag(name = "feature", descriptionGenerator = FeatureDescriptionGenerator.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface Feature {
    String value() default "";

    String description() default "";
}
