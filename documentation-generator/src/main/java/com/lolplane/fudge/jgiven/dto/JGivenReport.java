package com.lolplane.fudge.jgiven.dto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.tuple.Pair;

public class JGivenReport {

    private final Map<String, List<JGivenScenario>> scenariosByTag;
    private final Map<String, JGivenTag> tagsByTagId;
    private final List<JGivenTestClass> testClasses;

    public JGivenReport(List<JGivenTestClass> testClasses) {
        this(testClasses, prepareScenariosByTagsCache(testClasses), prepareTagsByIdCache(testClasses));
    }

    public JGivenReport(List<JGivenTestClass> testClasses, Map<String, List<JGivenScenario>> scenariosByTag, Map<String, JGivenTag> tagsByTagId) {
        this.testClasses = testClasses;
        this.scenariosByTag = scenariosByTag;
        this.tagsByTagId = tagsByTagId;
    }

    private static Map<String, List<JGivenScenario>> prepareScenariosByTagsCache(List<JGivenTestClass> testClasses) {
        return testClasses
            .stream()
            .flatMap(test -> test.scenarios().stream())
            .flatMap(scenario -> scenario.tagIds().stream().map(tagId -> Pair.of(tagId, scenario)))
            .collect(Collectors.groupingBy(Pair::getKey, Collectors.mapping(Pair::getValue, Collectors.toList())));
    }

    private static Map<String, JGivenTag> prepareTagsByIdCache(List<JGivenTestClass> testClasses) {
        return testClasses
            .stream()
            .flatMap(test -> test.tagMap().entrySet().stream().map(entry -> Pair.of(entry.getKey(), entry.getValue())))
            .distinct()
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

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

    public List<JGivenScenario> filterByTag(String tagType, String tagValue) {
        return filterByTagPredicate(entry -> Objects.equals(entry.getValue().type(), tagType) && Objects.equals(entry.getValue().value(), tagValue))
            .findFirst()
            .map(tag -> scenariosByTag.getOrDefault(tag.getKey(), List.of()))
            .orElse(List.of());
    }

    private Stream<Map.Entry<String, JGivenTag>> filterByTagPredicate(Predicate<Map.Entry<String, JGivenTag>> predicate) {
        return tagsByTagId
            .entrySet()
            .stream()
            .filter(predicate);
    }

    public List<JGivenScenario> filterByTag(String tagType) {
        return filterByTagPredicate(entry -> Objects.equals(entry.getValue().type(), tagType))
            .flatMap(entry -> scenariosByTag.getOrDefault(entry.getKey(), List.of()).stream())
            .distinct()
            .toList();
    }

    public List<JGivenTestClass> testClasses() {
        return Collections.unmodifiableList(testClasses);
    }

    public Stream<JGivenTestClass> testClasses(Predicate<JGivenTestClass> condition) {
        return testClasses.stream().filter(condition);
    }

    public Stream<JGivenTag> tags(Predicate<JGivenTag> condition) {
        return tagsByTagId.values().stream().filter(condition);
    }

}
