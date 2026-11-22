package com.bazaarehunar.patterns.behavioral.chainofresponsibility;

public class ItemsPresentHandler extends OrderValidationHandler {
    @Override
    protected ValidationResult check(OrderCheck order) {
        if (order.items() == null || order.items().isEmpty()) {
            return ValidationResult.fail("ItemsPresent", "Your order has no items");
        }
        if (order.items().size() > 50) {
            return ValidationResult.fail("ItemsPresent", "An order can contain at most 50 different items");
        }
        for (var entry : order.items().entrySet()) {
            if (entry.getValue() == null || entry.getValue() < 1 || entry.getValue() > 100) {
                return ValidationResult.fail("ItemsPresent", "Item quantity must be between 1 and 100");
            }
        }
        return ValidationResult.ok();
    }
}
