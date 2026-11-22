package com.bazaarehunar.patterns.behavioral.strategy;

public class DiscountPricingStrategy implements PricingStrategy {
    private final int percent;
    public DiscountPricingStrategy(int percent) { this.percent = percent; }
    @Override public int calculate(int subtotal) { return Math.max(0, subtotal - Math.round(subtotal * percent / 100f)); }
    @Override public String name() { return "DISCOUNT_" + percent; }
}
