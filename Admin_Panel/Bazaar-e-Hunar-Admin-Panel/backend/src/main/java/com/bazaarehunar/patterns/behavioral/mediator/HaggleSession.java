package com.bazaarehunar.patterns.behavioral.mediator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Concrete mediator for one product's negotiation. */
public class HaggleSession implements HaggleMediator {
    public enum Status { OPEN, ACCEPTED, REJECTED }

    private final String productName;
    private final int listPrice;
    private final BuyerParticipant buyer;
    private final SellerParticipant seller;
    private final List<String> transcript = new ArrayList<>();
    private Status status = Status.OPEN;
    private int agreedPrice = -1;

    public HaggleSession(String productName, int listPrice, BuyerParticipant buyer, SellerParticipant seller) {
        this.productName = productName;
        this.listPrice = listPrice;
        this.buyer = buyer;
        this.seller = seller;
        buyer.setMediator(this);
        seller.setMediator(this);
    }

    @Override
    public void relay(HaggleParticipant sender, String event, int amount) {
        if (status != Status.OPEN) {
            transcript.add("Session closed: ignored " + event + " from " + sender.name());
            return;
        }
        if (event.equals("OFFER") && (amount <= 0 || amount > listPrice)) {
            transcript.add(sender.name() + " made an invalid offer of " + amount);
            return;
        }
        transcript.add(sender.name() + " -> " + event + " " + amount);
        HaggleParticipant other = sender == buyer ? seller : buyer;

        switch (event) {
            case "ACCEPT" -> { status = Status.ACCEPTED; agreedPrice = amount; }
            case "REJECT" -> status = Status.REJECTED;
            default -> { }
        }
        other.receive(event, amount);
    }

    public String productName() { return productName; }
    public Status status() { return status; }
    public int agreedPrice() { return agreedPrice; }
    public List<String> transcript() { return Collections.unmodifiableList(transcript); }
}
