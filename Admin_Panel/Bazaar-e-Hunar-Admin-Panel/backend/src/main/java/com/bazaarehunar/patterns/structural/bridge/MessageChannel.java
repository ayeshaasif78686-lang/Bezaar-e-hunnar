package com.bazaarehunar.patterns.structural.bridge;

/** Implementor side of the bridge: HOW a message is delivered. */
public interface MessageChannel {
    String channelName();
    String deliver(String recipient, String subject, String body);
}
