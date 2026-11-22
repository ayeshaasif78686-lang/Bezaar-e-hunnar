package com.bazaarehunar.patterns.structural.flyweight;

public record CraftStyleFlyweight(String region, String material, String technique) {
    public String key() {
        return region + "|" + material + "|" + technique;
    }
}
