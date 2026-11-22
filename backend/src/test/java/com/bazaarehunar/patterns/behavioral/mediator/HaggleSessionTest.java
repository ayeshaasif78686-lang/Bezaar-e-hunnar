package com.bazaarehunar.patterns.behavioral.mediator;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HaggleSessionTest {
    private final BuyerParticipant buyer = new BuyerParticipant("Sara");
    private final SellerParticipant seller = new SellerParticipant("Bilal", 4000);
    private final HaggleSession session = new HaggleSession("Rug", 5000, buyer, seller);

    @Test
    void sellerAcceptsOfferAtOrAboveMinimum() {
        buyer.offer(4200);
        assertEquals(HaggleSession.Status.ACCEPTED, session.status());
        assertEquals(4200, session.agreedPrice());
    }

    @Test
    void counterOfferCanBeAcceptedByBuyer() {
        buyer.offer(3500);
        assertEquals(HaggleSession.Status.OPEN, session.status());
        buyer.acceptCounter();
        assertEquals(HaggleSession.Status.ACCEPTED, session.status());
        assertEquals(4000, session.agreedPrice());
    }

    @Test
    void lowOfferIsRejectedAndSessionCloses() {
        buyer.offer(1000);
        assertEquals(HaggleSession.Status.REJECTED, session.status());
        buyer.offer(4500);
        assertEquals(HaggleSession.Status.REJECTED, session.status());
        assertTrue(session.transcript().stream().anyMatch(line -> line.startsWith("Session closed")));
    }

    @Test
    void invalidOfferNeverReachesSeller() {
        buyer.offer(9999);
        assertTrue(seller.inbox().isEmpty());
        assertThrows(IllegalStateException.class, buyer::acceptCounter);
    }
}
