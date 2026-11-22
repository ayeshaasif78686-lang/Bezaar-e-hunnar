package com.bazaarehunar.patterns.behavioral.chainofresponsibility;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class OrderValidationChainTest {
    private final OrderValidationHandler chain = OrderValidationChain.standard();

    private OrderCheck order(Map<String, Integer> items, String address, String payment, int total) {
        return new OrderCheck(items, Map.of("rug", 5), "Ali", "0300", address, "Lahore", "Punjab", payment, total);
    }

    @Test
    void validOrderPassesWholeChain() {
        assertTrue(chain.handle(order(Map.of("rug", 2), "House 1", "COD", 5000)).valid());
    }

    @Test
    void firstFailingHandlerStopsTheChain() {
        assertEquals("ItemsPresent", chain.handle(order(Map.of(), "House 1", "COD", 1)).failedAt());
        assertEquals("Address", chain.handle(order(Map.of("rug", 1), " ", "COD", 1)).failedAt());
        assertEquals("Stock", chain.handle(order(Map.of("rug", 9), "House 1", "COD", 1)).failedAt());
        assertEquals("PaymentMethod", chain.handle(order(Map.of("rug", 1), "House 1", "", 1)).failedAt());
        assertEquals("FraudLimit", chain.handle(order(Map.of("rug", 1), "House 1", "COD", 600_000)).failedAt());
    }
}
