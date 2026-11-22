package com.bazaarehunar.patterns.structural.facade;

import com.bazaarehunar.patterns.behavioral.strategy.BulkPricingStrategy;
import com.bazaarehunar.patterns.behavioral.strategy.DiscountPricingStrategy;
import com.bazaarehunar.patterns.behavioral.strategy.PricingStrategy;
import com.bazaarehunar.patterns.behavioral.strategy.RegularPricingStrategy;
import com.bazaarehunar.patterns.creational.abstractfactory.CheckoutFactory;
import com.bazaarehunar.patterns.creational.abstractfactory.ShippingOption;
import com.bazaarehunar.patterns.creational.singleton.MarketplaceSettings;
import com.bazaarehunar.patterns.structural.adapter.PaymentGateway;
import com.bazaarehunar.patterns.structural.adapter.PaymentReceipt;
import com.bazaarehunar.patterns.structural.bridge.MessageChannel;
import com.bazaarehunar.patterns.structural.bridge.OrderConfirmationNotification;
import com.bazaarehunar.patterns.structural.decorator.PricedItem;

import java.util.List;
import java.util.Locale;

/**
 * FACADE (structural).
 * Checkout touches many subsystems: decorated item prices, pricing strategy, shipping (abstract factory),
 * payment (adapter) and customer notification (bridge). The facade offers ONE simple method,
 * {@link #checkout}, so controllers do not need to know how the pieces fit together.
 */
public class CheckoutFacade {
    private final CheckoutFactory checkoutFactory;
    private final PaymentGateway paymentGateway;
    private final MessageChannel notificationChannel;

    public CheckoutFacade(CheckoutFactory checkoutFactory, PaymentGateway paymentGateway,
                          MessageChannel notificationChannel) {
        this.checkoutFactory = checkoutFactory;
        this.paymentGateway = paymentGateway;
        this.notificationChannel = notificationChannel;
    }

    public CheckoutResult checkout(String orderNumber, String customerPhone, List<PricedItem> items,
                                   String coupon, boolean express) {
        if (items == null || items.isEmpty()) {
            return failure(orderNumber, "Your order has no items");
        }

        int subtotal = items.stream().mapToInt(PricedItem::price).sum();
        PricingStrategy strategy = chooseStrategy(subtotal, coupon);
        int afterPricing = strategy.calculate(subtotal);

        ShippingOption shipping = checkoutFactory.createShipping(express);
        MarketplaceSettings settings = MarketplaceSettings.getInstance();
        int shippingFee = subtotal >= settings.freeShippingThreshold() ? 0 : shipping.fee();
        int total = afterPricing + shippingFee;

        PaymentReceipt receipt = paymentGateway.charge(customerPhone, total);
        if (!receipt.success()) {
            return new CheckoutResult(orderNumber, false, subtotal, subtotal - afterPricing, shippingFee, total,
                    strategy.name(), shipping.carrier(), paymentGateway.gatewayName(), receipt, null,
                    receipt.message());
        }

        String notification = new OrderConfirmationNotification(notificationChannel, orderNumber, total)
                .send(customerPhone);
        return new CheckoutResult(orderNumber, true, subtotal, subtotal - afterPricing, shippingFee, total,
                strategy.name(), shipping.carrier(), paymentGateway.gatewayName(), receipt, notification,
                "Order placed");
    }

    private PricingStrategy chooseStrategy(int subtotal, String coupon) {
        String code = coupon == null ? "" : coupon.trim().toUpperCase(Locale.ROOT);
        if (code.equals("HUNAR10") || code.equals("WELCOME10")) return new DiscountPricingStrategy(10);
        if (subtotal >= MarketplaceSettings.getInstance().bulkDiscountThreshold()) return new BulkPricingStrategy();
        return new RegularPricingStrategy();
    }

    private CheckoutResult failure(String orderNumber, String message) {
        return new CheckoutResult(orderNumber, false, 0, 0, 0, 0, null, null, paymentGateway.gatewayName(),
                null, null, message);
    }
}
