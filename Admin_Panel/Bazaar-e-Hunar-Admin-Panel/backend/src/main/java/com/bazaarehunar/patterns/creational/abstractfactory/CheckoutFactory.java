package com.bazaarehunar.patterns.creational.abstractfactory;

import java.util.Locale;

/**
 * ABSTRACT FACTORY (creational).
 * Creates a matching FAMILY of checkout objects (shipping + payment) so that a domestic order can never be
 * mixed with international shipping or the other way round.
 */
public interface CheckoutFactory {
    ShippingOption createShipping(boolean express);
    PaymentOption createPayment();
    String region();

    static CheckoutFactory forCountry(String country) {
        String normalized = country == null ? "" : country.trim().toLowerCase(Locale.ROOT);
        if (normalized.isEmpty() || normalized.equals("pakistan") || normalized.equals("pk")) {
            return new DomesticCheckoutFactory();
        }
        return new InternationalCheckoutFactory();
    }
}
