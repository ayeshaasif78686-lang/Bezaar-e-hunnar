package com.bazaarehunar.patterns.structural.facade;

import com.bazaarehunar.patterns.structural.adapter.PaymentReceipt;

public record CheckoutResult(String orderNumber, boolean success, int subtotal, int discount, int shipping,
                             int total, String pricingStrategy, String carrier, String paymentMethod,
                             PaymentReceipt receipt, String notification, String message) { }
