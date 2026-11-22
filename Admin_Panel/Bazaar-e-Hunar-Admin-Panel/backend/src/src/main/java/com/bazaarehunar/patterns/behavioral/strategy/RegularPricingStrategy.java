package com.bazaarehunar.patterns.behavioral.strategy;

public class RegularPricingStrategy implements PricingStrategy {
    @Override public int calculate(int subtotal) { return subtotal; }
    @Override public String name() { return "REGULAR"; }
}
