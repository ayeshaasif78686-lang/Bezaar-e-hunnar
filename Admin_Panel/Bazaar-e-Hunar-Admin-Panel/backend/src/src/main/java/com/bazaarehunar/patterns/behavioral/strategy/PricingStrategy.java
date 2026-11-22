package com.bazaarehunar.patterns.behavioral.strategy;

public interface PricingStrategy {
    int calculate(int subtotal);
    String name();
}
