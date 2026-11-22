package com.bazaarehunar.patterns.showcase;

import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class PatternShowcaseTest {
    @Test
    void allTwentyThreePatternsAreListedWithCorrectCategoryCounts() {
        assertEquals(23, PatternCatalog.all().size());
        assertEquals(5L, PatternCatalog.count("Creational"));
        assertEquals(7L, PatternCatalog.count("Structural"));
        assertEquals(11L, PatternCatalog.count("Behavioral"));
        Set<String> names = PatternCatalog.all().stream().map(PatternInfo::name).collect(Collectors.toSet());
        assertEquals(23, names.size());
    }

    @Test
    void everyListedPatternHasARunnableDemo() {
        for (PatternInfo info : PatternCatalog.all()) {
            assertTrue(PatternDemos.exists(info.key()), "missing demo for " + info.key());
            Map<String, Object> result = PatternDemos.run(info.key());
            assertFalse(result.isEmpty(), "empty demo for " + info.key());
        }
        assertEquals(23, PatternDemos.runAll().size());
    }

    @Test
    void sampleDemosProduceExpectedValues() {
        assertEquals(Boolean.TRUE, PatternDemos.run("singleton").get("sameInstance"));
        assertEquals(Boolean.TRUE, PatternDemos.run("flyweight").get("sameObjectShared"));
        assertEquals(7, PatternDemos.run("visitor").get("preparationDays"));
    }
}
