package com.bazaarehunar.patterns.behavioral.state;

/**
 * STATE (behavioral).
 * An order behaves differently in each phase of its life (PLACED, PROCESSING, SHIPPED, DELIVERED,
 * CANCELLED - the same names used by OrderService). Each state class decides which transitions are legal;
 * illegal ones throw, so no big if/else on a status string is needed.
 */
public interface OrderState {
    String name();

    default OrderState process() { throw illegal("process"); }
    default OrderState ship() { throw illegal("ship"); }
    default OrderState deliver() { throw illegal("deliver"); }
    default OrderState cancel() { throw illegal("cancel"); }

    default boolean terminal() { return false; }

    private IllegalStateException illegal(String action) {
        return new IllegalStateException("Cannot " + action + " an order that is " + name());
    }
}
