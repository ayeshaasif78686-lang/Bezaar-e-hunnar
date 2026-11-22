package com.bazaarehunar.patterns.behavioral.strategy;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PricingStrategyTest {
    @Test
    void strategiesChangeSameSubtotal() {
        assertEquals(10000, new RegularPricingStrategy().calculate(10000));
        assertEquals(9000, new DiscountPricingStrategy(10).calculate(10000));
        assertEquals(9200, new BulkPricingStrategy().calculate(10000));
    }
}
