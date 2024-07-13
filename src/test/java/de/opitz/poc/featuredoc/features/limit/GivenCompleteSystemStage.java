package de.opitz.poc.featuredoc.features.limit;

import com.tngtech.jgiven.Stage;

public class GivenCompleteSystemStage extends Stage<GivenCompleteSystemStage> {
    public GivenCompleteSystemStage an_anonymous_user() {
        return self();
    }

    public GivenCompleteSystemStage a_configured_site_$(String siteName) {
        return self();
    }
}
