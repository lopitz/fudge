package de.opitz.poc.featuredoc.features.calculation;

import com.tngtech.jgiven.Stage;

public class ThenAwardCalculationStage extends Stage<ThenAwardCalculationStage> {
    public ThenAwardCalculationStage the_german_welcome_page_is_returned() {
        return self();
    }

    public ThenAwardCalculationStage the_award_accrued_is(int award) {
        return self();
    }
}
