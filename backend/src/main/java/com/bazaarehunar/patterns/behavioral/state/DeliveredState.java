package com.bazaarehunar.patterns.behavioral.state;

public class DeliveredState implements OrderState {
    @Override public String name() { return "DELIVERED"; }
    @Override public boolean terminal() { return true; }
}
