package com.bazaarehunar.patterns.behavioral.state;

public class CancelledState implements OrderState {
    @Override public String name() { return "CANCELLED"; }
    @Override public boolean terminal() { return true; }
}
