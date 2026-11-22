package com.bazaarehunar.patterns.structural.flyweight;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CraftStyleFactory {
    private final Map<String, CraftStyleFlyweight> cache = new ConcurrentHashMap<>();

    public CraftStyleFlyweight getStyle(String region, String material, String technique) {
        String key = region + "|" + material + "|" + technique;
        return cache.computeIfAbsent(key, ignored -> new CraftStyleFlyweight(region, material, technique));
    }

    public int cachedStyleCount() {
        return cache.size();
    }
}
