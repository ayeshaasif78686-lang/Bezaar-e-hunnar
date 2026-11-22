package com.bazaarehunar.patterns.behavioral.state;

public class ProcessingState implements OrderState {
    @Override public String name() { return "PROCESSING"; }
    @Override public OrderState ship() { return new ShippedState(); }
    @Override public OrderState cancel() { return new CancelledState(); }
}
