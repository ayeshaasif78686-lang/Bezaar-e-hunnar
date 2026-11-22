package com.bazaarehunar.patterns.behavioral.state;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/** Context of the State pattern: delegates every action to its current state object. */
public class OrderLifecycle {
    private OrderState state;
    private final List<String> history = new ArrayList<>();

    public OrderLifecycle() { this(new PlacedState()); }

    public OrderLifecycle(OrderState initial) {
        this.state = initial;
        history.add(initial.name());
    }

    /** Rebuilds the state object from the status string stored in the database. */
    public static OrderLifecycle fromStatus(String status) {
        String normalized = status == null ? "" : status.trim().toUpperCase(Locale.ROOT);
        return new OrderLifecycle(switch (normalized) {
            case "PLACED" -> new PlacedState();
            case "PROCESSING" -> new ProcessingState();
            case "SHIPPED" -> new ShippedState();
            case "DELIVERED" -> new DeliveredState();
            case "CANCELLED" -> new CancelledState();
            default -> throw new IllegalArgumentException("Unknown order status: " + status);
        });
    }

    public OrderLifecycle process() { return move(state.process()); }
    public OrderLifecycle ship() { return move(state.ship()); }
    public OrderLifecycle deliver() { return move(state.deliver()); }
    public OrderLifecycle cancel() { return move(state.cancel()); }

    public String status() { return state.name(); }
    public boolean finished() { return state.terminal(); }
    public List<String> history() { return Collections.unmodifiableList(history); }

    private OrderLifecycle move(OrderState next) {
        state = next;
        history.add(next.name());
        return this;
    }
}
