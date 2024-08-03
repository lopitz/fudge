package de.opitz.poc.featuredoc.features.calculation;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.Table;

public class GivenCompleteSystemStage extends Stage<GivenCompleteSystemStage> {
    public GivenCompleteSystemStage an_anonymous_user() {
        return self();
    }

    public GivenCompleteSystemStage events_resulting_in_awards(@Table EventWithAward... eventWithAwards) {
        return self();
    }

    public GivenCompleteSystemStage an_event(String name) {
        return self();
    }
}
