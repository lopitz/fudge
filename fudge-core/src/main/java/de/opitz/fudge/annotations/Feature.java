package de.opitz.fudge.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.tngtech.jgiven.annotation.IsTag;
import de.opitz.fudge.generators.FeatureDescriptionGenerator;

@IsTag(name = "feature", descriptionGenerator = FeatureDescriptionGenerator.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface Feature {
    String value() default "";

    String description() default "";
}
