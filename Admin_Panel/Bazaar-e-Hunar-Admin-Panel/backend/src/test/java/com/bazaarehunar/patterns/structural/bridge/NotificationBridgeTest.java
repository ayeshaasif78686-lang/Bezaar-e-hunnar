package com.bazaarehunar.patterns.structural.bridge;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NotificationBridgeTest {
    @Test
    void sameNotificationWorksOnEveryChannel() {
        assertTrue(new OrderConfirmationNotification(new EmailChannel(), "BEH1", 900).send("a@b.com").startsWith("EMAIL"));
        assertTrue(new OrderConfirmationNotification(new SmsChannel(), "BEH1", 900).send("0300").startsWith("SMS"));
        assertTrue(new OrderConfirmationNotification(new WhatsAppChannel(), "BEH1", 900).send("0300").startsWith("WHATSAPP"));
    }

    @Test
    void differentNotificationsShareOneChannel() {
        MessageChannel sms = new SmsChannel();
        assertTrue(new ShippingUpdateNotification(sms, "BEH2", "TCS").send("0300").contains("TCS"));
        assertTrue(new OrderConfirmationNotification(sms, "BEH2", 10).send("0300").contains("confirmed"));
    }

    @Test
    void smsTruncatesLongText() {
        String out = new SmsChannel().deliver("0300", "S", "x".repeat(300));
        assertTrue(out.endsWith("..."));
    }
}
