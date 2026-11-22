package com.bazaarehunar.patterns.structural.adapter;

/**
 * ADAPTER (structural).
 * Makes {@link LegacyEasyPaisaClient} usable through the {@link PaymentGateway} interface without changing
 * either side.
 */
public class EasyPaisaGatewayAdapter implements PaymentGateway {
    private final LegacyEasyPaisaClient client;

    public EasyPaisaGatewayAdapter(LegacyEasyPaisaClient client) {
        this.client = client;
    }

    @Override
    public PaymentReceipt charge(String payerPhone, int amountPkr) {
        String reference = client.sendMoney(payerPhone, amountPkr, "Bazaar-e-Hunar order");
        return reference == null
                ? new PaymentReceipt(gatewayName(), null, amountPkr, false, "EasyPaisa refused the transfer")
                : new PaymentReceipt(gatewayName(), reference, amountPkr, true, "Paid with EasyPaisa");
    }

    @Override
    public String gatewayName() { return "EasyPaisa"; }
}
