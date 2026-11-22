package com.bazaarehunar.patterns.structural.adapter;

/** Second adapter: converts rupees to paisa and status codes to a {@link PaymentReceipt}. */
public class JazzCashGatewayAdapter implements PaymentGateway {
    private final LegacyJazzCashSdk sdk;

    public JazzCashGatewayAdapter(LegacyJazzCashSdk sdk) {
        this.sdk = sdk;
    }

    @Override
    public PaymentReceipt charge(String payerPhone, int amountPkr) {
        int code = sdk.pay(payerPhone, amountPkr * 100L);
        return code == 0
                ? new PaymentReceipt(gatewayName(), sdk.lastReference(), amountPkr, true, "Paid with JazzCash")
                : new PaymentReceipt(gatewayName(), null, amountPkr, false, "JazzCash error code " + code);
    }

    @Override
    public String gatewayName() { return "JazzCash"; }
}
