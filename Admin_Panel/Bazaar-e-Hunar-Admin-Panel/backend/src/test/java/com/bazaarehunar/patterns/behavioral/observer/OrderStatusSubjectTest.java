package com.bazaarehunar.patterns.behavioral.observer;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OrderStatusSubjectTest {
    @Test
    void allObserversAreNotified() {
        OrderStatusSubject subject = new OrderStatusSubject();
        CustomerEmailObserver email = new CustomerEmailObserver();
        InventoryObserver inventory = new InventoryObserver();
        SellerDashboardObserver dashboard = new SellerDashboardObserver();
        subject.subscribe(email);
        subject.subscribe(inventory);
        subject.subscribe(dashboard);

        subject.changeStatus("BEH1", "PLACED");
        subject.changeStatus("BEH1", "CANCELLED");
        subject.changeStatus("BEH1", "CANCELLED"); // no change -> no event

        assertEquals(2, email.sentEmails().size());
        assertEquals(java.util.List.of("BEH1"), inventory.restockedOrders());
        assertEquals(1, dashboard.counters().get("CANCELLED"));
        assertEquals(0, dashboard.counters().get("PLACED"));
    }

    @Test
    void unsubscribedObserverStopsReceiving() {
        OrderStatusSubject subject = new OrderStatusSubject();
        CustomerEmailObserver email = new CustomerEmailObserver();
        subject.subscribe(email);
        subject.unsubscribe(email);
        subject.changeStatus("BEH2", "PLACED");
        assertTrue(email.sentEmails().isEmpty());
        assertEquals(0, subject.observerCount());
        assertEquals("PLACED", subject.statusOf("BEH2"));
    }
}
