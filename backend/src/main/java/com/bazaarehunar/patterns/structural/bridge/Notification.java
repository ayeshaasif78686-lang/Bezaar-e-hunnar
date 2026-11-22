package com.bazaarehunar.patterns.structural.bridge;

/**
 * BRIDGE (structural).
 * Splits "WHAT we tell the customer" (this abstraction hierarchy) from "HOW we deliver it"
 * ({@link MessageChannel} hierarchy). Any notification type works with any channel, so 2 notification types
 * and 3 channels need 5 classes instead of 6, and a new channel needs no change to the notifications.
 */
public abstract class Notification {
    protected final MessageChannel channel;

    protected Notification(MessageChannel channel) {
        this.channel = channel;
    }

    protected abstract String subject();
    protected abstract String body();

    public String send(String recipient) {
        return channel.deliver(recipient, subject(), body());
    }
}
