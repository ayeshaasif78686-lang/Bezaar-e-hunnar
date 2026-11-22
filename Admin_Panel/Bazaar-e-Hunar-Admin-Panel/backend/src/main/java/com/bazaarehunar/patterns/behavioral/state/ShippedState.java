package com.bazaarehunar.patterns.behavioral.state;

public class ShippedState implements OrderState {
    @Override public String name() { return "SHIPPED"; }
    @Override public OrderState deliver() { return new DeliveredState(); }
}
