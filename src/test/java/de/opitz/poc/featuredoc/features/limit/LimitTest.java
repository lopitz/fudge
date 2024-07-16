package de.opitz.poc.featuredoc.features.limit;

import com.tngtech.jgiven.integration.spring.junit5.SpringScenarioTest;
import de.opitz.poc.featuredoc.TestConfiguration;
import de.opitz.poc.featuredoc.jgiven.Feature;
import de.opitz.poc.featuredoc.jgiven.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@Feature("yearly limit")
@SpringBootTest(classes = TestConfiguration.class)
@SuppressWarnings("java:S2699") // lopitz: Assertions are handled via the Then stage and the then() keyword
class LimitTest extends SpringScenarioTest<GivenCompleteSystemStage, WhenLimitStage, ThenLimitStage> {

    @Test
    @DisplayName("Provides a welcome page")
    @Story({"JUSTDE-2311", "JUSTDE-2412"})
    void providesAWelcomePage() {
        given()
            .an_anonymous_user().and()
            .a_configured_site_$_on_$("Germany", "mWeb");

        when()
            .requesting_the_welcome_page();

        then()
            .the_german_welcome_page_is_returned();
    }

}
