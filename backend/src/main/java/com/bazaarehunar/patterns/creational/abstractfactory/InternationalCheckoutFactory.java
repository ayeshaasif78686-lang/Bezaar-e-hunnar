package com.bazaarehunar.patterns.creational.abstractfactory;

/** Concrete factory 2: overseas buyers (courier + card payment). */
public class InternationalCheckoutFactory implements CheckoutFactory {

    @Override
    public ShippingOption createShipping(boolean express) {
        return express
                ? new InternationalCourier("DHL Express", 4500, "3-5 Business Days")
                : new InternationalCourier("DHL eCommerce", 2500, "7-14 Business Days");
    }

    @Override
    public PaymentOption createPayment() {
        return new CardPayment();
    }

    @Override
    public String region() { return "INTERNATIONAL"; }

    public record InternationalCourier(String carrier, int fee, String estimate) implements ShippingOption { }

    public static class CardPayment implements PaymentOption {
        @Override public String name() { return "Credit / Debit Card"; }
        @Override public String instructions() { return "Pay securely online. Card numbers are never stored."; }
        @Override public boolean acceptsOnline() { return true; }
    }
}
