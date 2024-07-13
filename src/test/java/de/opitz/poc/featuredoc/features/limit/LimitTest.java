package de.opitz.poc.featuredoc.features.limit;

import com.tngtech.jgiven.integration.spring.junit5.SpringScenarioTest;
import de.opitz.poc.featuredoc.TestConfiguration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = TestConfiguration.class)
@SuppressWarnings("java:S2699") // lopitz: Assertions are handled via the Then stage and the then() keyword
class LimitTest extends SpringScenarioTest<GivenCompleteSystemStage, WhenLimitStage, ThenLimitStage> {

    @Test
    @DisplayName("Provides a welcome page")
    void providesAWelcomePage() {
        given()
            .an_anonymous_user().and()
            .a_configured_site_$("Germany");

        when()
            .requesting_the_welcome_page();

        then()
            .the_german_welcome_page_is_returned();
    }

}
