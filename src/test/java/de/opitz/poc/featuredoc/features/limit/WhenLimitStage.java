package de.opitz.poc.featuredoc.features.limit;

import com.tngtech.jgiven.Stage;

public class WhenLimitStage extends Stage<WhenLimitStage> {
    public WhenLimitStage requesting_the_welcome_page() {
        return self();
    }
}
