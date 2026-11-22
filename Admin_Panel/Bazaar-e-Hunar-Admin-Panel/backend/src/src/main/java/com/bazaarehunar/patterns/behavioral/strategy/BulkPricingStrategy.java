package com.bazaarehunar.patterns.behavioral.strategy;

public class BulkPricingStrategy implements PricingStrategy {
    @Override public int calculate(int subtotal) { return subtotal >= 10000 ? Math.round(subtotal * 0.92f) : subtotal; }
    @Override public String name() { return "BULK"; }
}
