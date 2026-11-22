package com.bazaarehunar.patterns.behavioral.mediator;

/**
 * MEDIATOR (behavioral).
 * Buyer and seller never talk to each other directly during a haggle. Both only know the mediator, which
 * applies the rules (offer limits, counter-offers, closing the session) and routes the messages.
 */
public interface HaggleMediator {
    void relay(HaggleParticipant sender, String event, int amount);
}
