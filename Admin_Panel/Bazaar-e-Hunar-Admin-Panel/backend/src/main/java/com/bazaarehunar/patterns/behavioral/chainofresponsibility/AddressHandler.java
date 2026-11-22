package com.bazaarehunar.patterns.behavioral.chainofresponsibility;

public class AddressHandler extends OrderValidationHandler {
    @Override
    protected ValidationResult check(OrderCheck o) {
        if (blank(o.fullName()) || blank(o.phone()) || blank(o.address()) || blank(o.city()) || blank(o.province())) {
            return ValidationResult.fail("Address", "Add your shipping address before placing the order");
        }
        return ValidationResult.ok();
    }
}
