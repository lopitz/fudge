package de.opitz.fudge.features.limit;

import com.tngtech.jgiven.Stage;

public class ThenLimitStage extends Stage<ThenLimitStage> {
    public ThenLimitStage the_german_welcome_page_is_returned() {
        return self();
    }
}
