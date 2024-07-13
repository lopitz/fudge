package de.opitz.poc.featuredoc;

import com.tngtech.jgiven.integration.spring.EnableJGiven;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@EnableJGiven
@Configuration
@ComponentScan("de.opitz.poc.featuredoc")
public class TestConfiguration {
}
