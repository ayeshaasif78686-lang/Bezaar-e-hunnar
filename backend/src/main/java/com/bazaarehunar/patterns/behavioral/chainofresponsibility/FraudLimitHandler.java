package com.bazaarehunar.patterns.behavioral.chainofresponsibility;

/** Very large orders need manual review before they can be processed automatically. */
public class FraudLimitHandler extends OrderValidationHandler {
    public static final int MANUAL_REVIEW_LIMIT = 500_000;

    @Override
    protected ValidationResult check(OrderCheck order) {
        if (order.total() > MANUAL_REVIEW_LIMIT) {
            return ValidationResult.fail("FraudLimit", "Orders above PKR " + MANUAL_REVIEW_LIMIT + " need manual review");
        }
        return ValidationResult.ok();
    }
}
