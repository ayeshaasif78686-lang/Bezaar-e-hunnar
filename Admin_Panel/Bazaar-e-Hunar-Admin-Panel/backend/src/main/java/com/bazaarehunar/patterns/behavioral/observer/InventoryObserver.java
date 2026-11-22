package com.bazaarehunar.patterns.behavioral.observer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Releases reserved stock when an order is cancelled. */
public class InventoryObserver implements OrderObserver {
    private final List<String> restockedOrders = new ArrayList<>();

    @Override
    public void onStatusChanged(OrderEvent event) {
        if ("CANCELLED".equals(event.newStatus())) restockedOrders.add(event.orderNumber());
    }

    public List<String> restockedOrders() { return Collections.unmodifiableList(restockedOrders); }
}
