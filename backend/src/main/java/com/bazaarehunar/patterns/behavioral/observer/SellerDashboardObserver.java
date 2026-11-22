package com.bazaarehunar.patterns.behavioral.observer;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

/** Keeps live counters (per status) for the seller dashboard. */
public class SellerDashboardObserver implements OrderObserver {
    private final Map<String, Integer> counters = new TreeMap<>();

    @Override
    public void onStatusChanged(OrderEvent event) {
        if (event.oldStatus() != null) counters.merge(event.oldStatus(), -1, Integer::sum);
        counters.merge(event.newStatus(), 1, Integer::sum);
    }

    public Map<String, Integer> counters() { return Collections.unmodifiableMap(counters); }
}
