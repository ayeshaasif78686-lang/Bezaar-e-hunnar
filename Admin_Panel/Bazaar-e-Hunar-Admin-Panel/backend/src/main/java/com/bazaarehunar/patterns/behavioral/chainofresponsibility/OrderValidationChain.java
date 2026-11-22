package com.bazaarehunar.patterns.behavioral.chainofresponsibility;

/** Assembles the standard chain: items -> address -> stock -> payment -> fraud limit. */
public final class OrderValidationChain {
    private OrderValidationChain() { }

    public static OrderValidationHandler standard() {
        OrderValidationHandler first = new ItemsPresentHandler();
        first.linkWith(new AddressHandler())
                .linkWith(new StockHandler())
                .linkWith(new PaymentMethodHandler())
                .linkWith(new FraudLimitHandler());
        return first;
    }
}
