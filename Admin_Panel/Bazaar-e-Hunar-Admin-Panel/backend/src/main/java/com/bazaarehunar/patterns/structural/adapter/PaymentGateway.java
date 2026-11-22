package com.bazaarehunar.patterns.structural.adapter;

/** Target interface: the only payment API the marketplace code knows about. */
public interface PaymentGateway {
    PaymentReceipt charge(String payerPhone, int amountPkr);
    String gatewayName();
}
