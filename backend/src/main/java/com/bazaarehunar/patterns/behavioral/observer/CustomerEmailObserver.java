package com.bazaarehunar.patterns.behavioral.observer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CustomerEmailObserver implements OrderObserver {
    private final List<String> sentEmails = new ArrayList<>();

    @Override
    public void onStatusChanged(OrderEvent event) {
        sentEmails.add("Order " + event.orderNumber() + " is now " + event.newStatus());
    }

    public List<String> sentEmails() { return Collections.unmodifiableList(sentEmails); }
}
