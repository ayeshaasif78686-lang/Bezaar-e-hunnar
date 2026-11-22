package com.bazaarehunar.patterns.behavioral.mediator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Colleague base class. */
public abstract class HaggleParticipant {
    protected final String name;
    protected HaggleMediator mediator;
    private final List<String> inbox = new ArrayList<>();

    protected HaggleParticipant(String name) { this.name = name; }

    void setMediator(HaggleMediator mediator) { this.mediator = mediator; }
    public String name() { return name; }
    public List<String> inbox() { return Collections.unmodifiableList(inbox); }

    public final void receive(String event, int amount) {
        inbox.add(event + " " + amount);
        onReceive(event, amount);
    }

    protected abstract void onReceive(String event, int amount);
}
