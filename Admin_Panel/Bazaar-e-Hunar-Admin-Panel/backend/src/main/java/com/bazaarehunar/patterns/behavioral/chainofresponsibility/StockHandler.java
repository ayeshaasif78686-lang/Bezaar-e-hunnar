package com.bazaarehunar.patterns.behavioral.chainofresponsibility;

public class StockHandler extends OrderValidationHandler {
    @Override
    protected ValidationResult check(OrderCheck order) {
        for (var entry : order.items().entrySet()) {
            int available = order.stock() == null ? 0 : order.stock().getOrDefault(entry.getKey(), 0);
            if (entry.getValue() > available) {
                return ValidationResult.fail("Stock", "Only " + available + " left of " + entry.getKey());
            }
        }
        return ValidationResult.ok();
    }
}
