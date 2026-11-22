package com.bazaarehunar.patterns.behavioral.observer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.List;

/**
 * OBSERVER (behavioral) - the subject.
 * Changing an order's status notifies every subscribed observer (customer e-mail, inventory, seller
 * dashboard ...). The subject does not know what the observers do, so new reactions need no change here.
 */
public class OrderStatusSubject {
    private final List<OrderObserver> observers = new CopyOnWriteArrayList<>();
    private final Map<String, String> statuses = new ConcurrentHashMap<>();

    public void subscribe(OrderObserver observer) { observers.add(observer); }
    public void unsubscribe(OrderObserver observer) { observers.remove(observer); }
    public int observerCount() { return observers.size(); }

    public String statusOf(String orderNumber) { return statuses.get(orderNumber); }

    public void changeStatus(String orderNumber, String newStatus) {
        String old = statuses.put(orderNumber, newStatus);
        if (newStatus.equals(old)) return;
        OrderEvent event = new OrderEvent(orderNumber, old, newStatus);
        for (OrderObserver observer : observers) {
            observer.onStatusChanged(event);
        }
    }
}
