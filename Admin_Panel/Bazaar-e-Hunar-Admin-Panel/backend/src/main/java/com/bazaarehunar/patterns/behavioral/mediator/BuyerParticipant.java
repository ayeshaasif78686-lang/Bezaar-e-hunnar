package com.bazaarehunar.patterns.behavioral.mediator;

public class BuyerParticipant extends HaggleParticipant {
    private int lastCounter = -1;

    public BuyerParticipant(String name) { super(name); }

    public void offer(int amount) { mediator.relay(this, "OFFER", amount); }

    /** Accepts the seller's latest counter-offer. */
    public void acceptCounter() {
        if (lastCounter < 0) throw new IllegalStateException("There is no counter-offer to accept");
        mediator.relay(this, "ACCEPT", lastCounter);
    }

    @Override
    protected void onReceive(String event, int amount) {
        if (event.equals("COUNTER")) lastCounter = amount;
    }
}
