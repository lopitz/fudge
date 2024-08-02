package de.opitz.poc.featuredoc.jgiven.external.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.tngtech.jgiven.annotation.IsTag;
import de.opitz.poc.featuredoc.jgiven.external.generators.FeatureDescriptionGenerator;

@IsTag(name = "feature", descriptionGenerator = FeatureDescriptionGenerator.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface Feature {
    String value() default "";

    String description() default "";
}
