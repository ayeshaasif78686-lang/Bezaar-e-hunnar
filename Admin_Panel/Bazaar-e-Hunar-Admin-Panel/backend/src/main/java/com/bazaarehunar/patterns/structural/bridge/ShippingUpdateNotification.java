package com.bazaarehunar.patterns.structural.bridge;

public class ShippingUpdateNotification extends Notification {
    private final String orderNumber;
    private final String carrier;

    public ShippingUpdateNotification(MessageChannel channel, String orderNumber, String carrier) {
        super(channel);
        this.orderNumber = orderNumber;
        this.carrier = carrier;
    }

    @Override protected String subject() { return "Order " + orderNumber + " shipped"; }
    @Override protected String body() { return "Your parcel is with " + carrier + "."; }
}
