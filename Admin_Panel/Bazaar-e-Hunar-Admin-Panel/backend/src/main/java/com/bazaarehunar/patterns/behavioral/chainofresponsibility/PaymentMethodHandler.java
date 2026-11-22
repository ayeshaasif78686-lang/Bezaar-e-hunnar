package com.bazaarehunar.patterns.behavioral.chainofresponsibility;

public class PaymentMethodHandler extends OrderValidationHandler {
    @Override
    protected ValidationResult check(OrderCheck order) {
        if (blank(order.paymentMethod())) {
            return ValidationResult.fail("PaymentMethod", "Choose a payment method");
        }
        return ValidationResult.ok();
    }
}
