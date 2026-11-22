package com.bazaarehunar.patterns.structural.bridge;

public class OrderConfirmationNotification extends Notification {
    private final String orderNumber;
    private final int total;

    public OrderConfirmationNotification(MessageChannel channel, String orderNumber, int total) {
        super(channel);
        this.orderNumber = orderNumber;
        this.total = total;
    }

    @Override protected String subject() { return "Order " + orderNumber + " confirmed"; }
    @Override protected String body() { return "Thank you! Your total is PKR " + total + "."; }
}
