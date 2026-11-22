package com.bazaarehunar.patterns.structural.decorator;

/** Component interface of the Decorator pattern. */
public interface PricedItem {
    int price();
    String description();
}
