package com.bazaarehunar.patterns.structural.bridge;

public class SmsChannel implements MessageChannel {
    @Override public String channelName() { return "SMS"; }
    @Override public String deliver(String recipient, String subject, String body) {
        String text = subject + ": " + body;
        if (text.length() > 160) text = text.substring(0, 157) + "...";
        return "SMS to " + recipient + " | " + text;
    }
}
