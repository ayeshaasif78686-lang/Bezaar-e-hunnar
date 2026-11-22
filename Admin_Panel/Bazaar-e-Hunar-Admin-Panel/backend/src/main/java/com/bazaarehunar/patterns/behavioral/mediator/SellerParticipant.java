package com.bazaarehunar.patterns.behavioral.mediator;

/** Seller with a private minimum price. Offers within 15% below it get a counter-offer. */
public class SellerParticipant extends HaggleParticipant {
    private final int minimumPrice;

    public SellerParticipant(String name, int minimumPrice) {
        super(name);
        this.minimumPrice = minimumPrice;
    }

    @Override
    protected void onReceive(String event, int amount) {
        if (!event.equals("OFFER")) return;
        if (amount >= minimumPrice) {
            mediator.relay(this, "ACCEPT", amount);
        } else if (amount >= Math.round(minimumPrice * 0.85f)) {
            mediator.relay(this, "COUNTER", minimumPrice);
        } else {
            mediator.relay(this, "REJECT", amount);
        }
    }
}
