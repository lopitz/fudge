package de.opitz.poc.featuredoc.jgiven;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.tngtech.jgiven.annotation.IsTag;

@IsTag(name = "feature")
@Retention(RetentionPolicy.RUNTIME)
public @interface Feature {
    String value() default "";

    String description() default "";

    String descriptionHref() default "";
}
