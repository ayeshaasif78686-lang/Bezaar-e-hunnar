package com.bazaarehunar.patterns.structural.decorator;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PriceDecoratorTest {
    @Test
    void decoratorsStack() {
        PricedItem item = new GiftWrapDecorator(new EmbroideryDecorator(new BasePricedItem("Shawl", 4000), "Ali"));
        assertEquals(4000 + 300 + 60 + 250, item.price());
        assertEquals("Shawl + embroidery \"Ali\" + gift wrap", item.description());
    }

    @Test
    void discountAppliesToWrappedPrice() {
        PricedItem item = new FestivalDiscountDecorator(new GiftWrapDecorator(new BasePricedItem("Vase", 1750)), 10);
        assertEquals(1800, item.price());
        assertThrows(IllegalArgumentException.class, () -> new FestivalDiscountDecorator(item, 95));
    }
}
