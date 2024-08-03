package de.opitz.poc.featuredoc.features.limit;

import com.tngtech.jgiven.integration.spring.junit5.SpringScenarioTest;
import de.opitz.poc.featuredoc.TestConfiguration;
import de.opitz.poc.featuredoc.jgiven.external.annotations.Feature;
import de.opitz.poc.featuredoc.jgiven.external.annotations.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@Feature(
    value = "yearly limit",
    description = """
        This is just a description in Markdown syntax.

        It should tell me, whether this is feasible and good-looking. For that I'm trying to come up with some longer text, to showcase what could happen in reality,
        if we included feature documentation in the @Feature annotation.

        I will now copy some text from the internet, because I don't know what I should write about...

        > Language-specific OpenFeature SDK implementations **SHOULD** expose an in-memory provider built into the SDK.

        The in-memory provider is intended to be used for testing; SDK consumers may use it for their use cases.
        Hence, the packaging, naming, and access modifiers must be set appropriately.

        Given below are features this provider **MUST** support,

        - Provider must be initiated with a pre-defined set of flags provided to a constructor
        - Feature Flag structure must be minimal but should help to test OpenFeature specification
        - EvaluationContext support should be provided through callbacks/lambda expressions
        - Provider must support a means of updating flag values, resulting in the emission of `PROVIDER_CONFIGURATION_CHANGED` events
        - Provider must be maintained to support specification changes

        ## SDK end-to-end testing

        > E2E tests must utilize [in-memory provider](#in-memory-provider) defined within the SDK and must be self-contained.

        OpenFeature project maintains an end-to-end(e2e) test suite defined with [Gherkin syntax](https://cucumber.io/docs/gherkin/).
        These test definitions reside in [Appendix B](./appendix-b-gherkin-suites.md)

        ```mermaid
        flowchart LR\s
            subgraph SDK\s
            A[e2e Tests] -.-> B[In-memory provider]
            end
        ```

        ## Multi-Provider

        ### Introduction

        The OpenFeature Multi-Provider wraps multiple underlying providers in a unified interface, allowing the SDK client to transparently interact with all those providers at once.
        This allows use cases where a single client and evaluation interface is desired, but where the flag data should come from more than one source.

        Some examples:

        - A migration from one feature flagging provider to another.
          During that process, you may have some flags that have been ported to the new system and others that haven’t.
          Therefore you’d want the Multi-Provider to return the result of the “new” system if available otherwise, return the "old" system’s result.
        - Long-term use of multiple sources for flags.
          For example, someone might want to be able to combine environment variables, database entries, and vendor feature flag results together in a single interface, and define the precedence order in which those sources should be consulted.

        Check the [OpenFeature JavaScript Multi-Provider](https://github.com/open-feature/js-sdk-contrib/tree/main/libs/providers/multi-provider) for a reference implementation.

        ### Basics

        The provider is initialized by passing a list of provider instances it should evaluate.
        The order of the array defines the order in which sources should be evaluated.
        The provider whose value is ultimately used will depend on the “strategy” that is provided, which can be chosen from a set of pre-defined ones or implemented as custom logic.

        For example:

        ```typescript
        const multiProvider = new MultiProvider(
         [
          {
           provider: new ProviderA(),
          },
          {
           provider: new ProviderB()
          }
         ],
         new FirstMatchStrategy()
        )
        ```
        """)
@SpringBootTest(classes = TestConfiguration.class)
@SuppressWarnings("java:S2699") // lopitz: Assertions are handled via the Then stage and the then() keyword
class LimitTest extends SpringScenarioTest<GivenCompleteSystemStage, WhenLimitStage, ThenLimitStage> {

    @Test
    @DisplayName("Provides a welcome page")
    @Story({"FEATUREDOCS-2311", "FEATUREDOCS-2412"})
    void providesAWelcomePage() {
        given()
            .an_anonymous_user().and()
            .a_configured_site_$_on_$("Germany", "mWeb");

        when()
            .requesting_the_welcome_page();

        then()
            .the_german_welcome_page_is_returned();
    }

    @Test
    @DisplayName("Different story")
    @Story({"FEATUREDOCS-1011"})
    void a_different_story() {
        given()
            .an_anonymous_user().and()
            .a_configured_site_$_on_$("Germany", "mWeb");

        when()
            .requesting_the_welcome_page();

        then()
            .the_german_welcome_page_is_returned();
    }

}
