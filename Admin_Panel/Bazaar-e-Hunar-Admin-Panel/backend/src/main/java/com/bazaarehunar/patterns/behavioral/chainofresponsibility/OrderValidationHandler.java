package com.bazaarehunar.patterns.behavioral.chainofresponsibility;

/**
 * CHAIN OF RESPONSIBILITY (behavioral).
 * Each handler checks one rule and either rejects the order or passes it to the next handler. New rules are
 * added by linking one more handler; the caller only talks to the first one.
 */
public abstract class OrderValidationHandler {
    private OrderValidationHandler next;

    public OrderValidationHandler linkWith(OrderValidationHandler next) {
        this.next = next;
        return next;
    }

    public final ValidationResult handle(OrderCheck order) {
        ValidationResult result = check(order);
        if (!result.valid()) return result;
        return next == null ? ValidationResult.ok() : next.handle(order);
    }

    protected abstract ValidationResult check(OrderCheck order);

    protected static boolean blank(String value) {
        return value == null || value.isBlank();
    }
}
