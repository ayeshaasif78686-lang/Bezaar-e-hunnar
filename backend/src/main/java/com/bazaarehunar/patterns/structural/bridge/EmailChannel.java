package com.bazaarehunar.patterns.structural.bridge;

public class EmailChannel implements MessageChannel {
    @Override public String channelName() { return "EMAIL"; }
    @Override public String deliver(String recipient, String subject, String body) {
        return "EMAIL to " + recipient + " | " + subject + " | " + body;
    }
}
