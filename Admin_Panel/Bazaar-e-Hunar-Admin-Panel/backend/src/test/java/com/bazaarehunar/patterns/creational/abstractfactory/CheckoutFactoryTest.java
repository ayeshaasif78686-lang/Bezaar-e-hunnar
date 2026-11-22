package com.bazaarehunar.patterns.creational.abstractfactory;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CheckoutFactoryTest {
    @Test
    void domesticFamilyIsConsistent() {
        CheckoutFactory factory = CheckoutFactory.forCountry("Pakistan");
        assertEquals("DOMESTIC", factory.region());
        assertEquals(300, factory.createShipping(false).fee());
        assertEquals(600, factory.createShipping(true).fee());
        assertFalse(factory.createPayment().acceptsOnline());
    }

    @Test
    void internationalFamilyIsConsistent() {
        CheckoutFactory factory = CheckoutFactory.forCountry("Canada");
        assertEquals("INTERNATIONAL", factory.region());
        assertTrue(factory.createShipping(true).carrier().startsWith("DHL"));
        assertTrue(factory.createPayment().acceptsOnline());
    }
}
