package de.opitz.poc.featuredoc.jgiven.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public record JGivenReport(List<JGivenTestClass> testClasses) {
    public static JGivenReport empty() {
        return new JGivenReport(List.of());
    }

    public JGivenReport withTestClass(JGivenTestClass testClass) {
        var newTestClasses = new ArrayList<>(Objects.requireNonNullElseGet(testClasses, List::<JGivenTestClass>of));
        newTestClasses.add(testClass);
        return new JGivenReport(newTestClasses);
    }

    public JGivenReport join(JGivenReport other) {
        var newTestClasses = new ArrayList<>(Objects.requireNonNullElseGet(testClasses, List::<JGivenTestClass>of));
        newTestClasses.addAll(other.testClasses);
        return new JGivenReport(newTestClasses);
    }
}
