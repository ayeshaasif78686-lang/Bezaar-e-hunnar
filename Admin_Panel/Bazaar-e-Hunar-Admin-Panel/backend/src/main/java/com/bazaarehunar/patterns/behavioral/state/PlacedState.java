package com.bazaarehunar.patterns.behavioral.state;

public class PlacedState implements OrderState {
    @Override public String name() { return "PLACED"; }
    @Override public OrderState process() { return new ProcessingState(); }
    @Override public OrderState cancel() { return new CancelledState(); }
}
