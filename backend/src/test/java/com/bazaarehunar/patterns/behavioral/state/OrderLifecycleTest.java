package com.bazaarehunar.patterns.behavioral.state;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrderLifecycleTest {
    @Test
    void happyPath() {
        OrderLifecycle order = new OrderLifecycle().process().ship().deliver();
        assertEquals("DELIVERED", order.status());
        assertTrue(order.finished());
        assertEquals(List.of("PLACED", "PROCESSING", "SHIPPED", "DELIVERED"), order.history());
    }

    @Test
    void cancelOnlyBeforeShipping() {
        assertEquals("CANCELLED", new OrderLifecycle().cancel().status());
        assertEquals("CANCELLED", new OrderLifecycle().process().cancel().status());
        assertThrows(IllegalStateException.class, () -> new OrderLifecycle().process().ship().cancel());
    }

    @Test
    void illegalTransitionsThrow() {
        assertThrows(IllegalStateException.class, () -> new OrderLifecycle().ship());
        assertThrows(IllegalStateException.class, () -> new OrderLifecycle().cancel().process());
        assertThrows(IllegalStateException.class, () -> OrderLifecycle.fromStatus("delivered").deliver());
    }

    @Test
    void restoresFromDatabaseStatus() {
        assertEquals("SHIPPED", OrderLifecycle.fromStatus("shipped").status());
        assertThrows(IllegalArgumentException.class, () -> OrderLifecycle.fromStatus("LOST"));
    }
}
