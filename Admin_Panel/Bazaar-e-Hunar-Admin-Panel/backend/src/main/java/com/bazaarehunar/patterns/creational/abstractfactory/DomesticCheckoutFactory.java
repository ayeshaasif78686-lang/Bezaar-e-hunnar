package com.bazaarehunar.patterns.creational.abstractfactory;

import com.bazaarehunar.patterns.creational.singleton.MarketplaceSettings;

/** Concrete factory 1: Pakistan. Fees come from the {@link MarketplaceSettings} singleton. */
public class DomesticCheckoutFactory implements CheckoutFactory {

    @Override
    public ShippingOption createShipping(boolean express) {
        MarketplaceSettings settings = MarketplaceSettings.getInstance();
        return express
                ? new DomesticCourier("TCS Overnight", settings.expressShippingFee(), "1-3 Business Days")
                : new DomesticCourier("Leopards Standard", settings.standardShippingFee(), "3-7 Business Days");
    }

    @Override
    public PaymentOption createPayment() {
        return new CashOnDeliveryPayment();
    }

    @Override
    public String region() { return "DOMESTIC"; }

    public record DomesticCourier(String carrier, int fee, String estimate) implements ShippingOption { }

    public static class CashOnDeliveryPayment implements PaymentOption {
        @Override public String name() { return "Cash on Delivery"; }
        @Override public String instructions() { return "Pay the rider in cash when the parcel arrives."; }
        @Override public boolean acceptsOnline() { return false; }
    }
}
