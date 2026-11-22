package com.bazaarehunar.patterns.behavioral.observer;

/** OBSERVER (behavioral): anything that wants to react when an order's status changes. */
public interface OrderObserver {
    void onStatusChanged(OrderEvent event);
}
