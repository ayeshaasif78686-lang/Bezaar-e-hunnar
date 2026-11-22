package com.bazaarehunar.patterns.behavioral.visitor;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CartLineVisitorTest {
    private final List<CartLine> cart = List.of(
            new ReadyMadeLine("Mug", 800, 2),
            new CustomMadeLine("Shawl", 4000, 1, 500, 7),
            new GiftBoxLine("Festive box", 300, 1));

    @Test
    void totalVisitor() {
        TotalPriceVisitor visitor = new TotalPriceVisitor();
        cart.forEach(line -> line.accept(visitor));
        assertEquals(1600 + 4500 + 300, visitor.total());
    }

    @Test
    void preparationTimeVisitorUsesSlowestLine() {
        PreparationTimeVisitor visitor = new PreparationTimeVisitor();
        cart.forEach(line -> line.accept(visitor));
        assertEquals(7, visitor.days());
    }

    @Test
    void receiptVisitor() {
        ReceiptVisitor visitor = new ReceiptVisitor();
        cart.forEach(line -> line.accept(visitor));
        assertEquals(3, visitor.lines().size());
        assertEquals("2 x Mug @ 800", visitor.lines().get(0));
        assertTrue(visitor.lines().get(1).contains("custom"));
    }
}
