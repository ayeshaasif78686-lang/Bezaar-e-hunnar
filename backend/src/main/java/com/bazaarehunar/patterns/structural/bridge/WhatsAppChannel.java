package com.bazaarehunar.patterns.structural.bridge;

public class WhatsAppChannel implements MessageChannel {
    @Override public String channelName() { return "WHATSAPP"; }
    @Override public String deliver(String recipient, String subject, String body) {
        return "WHATSAPP to " + recipient + " | *" + subject + "* " + body;
    }
}
