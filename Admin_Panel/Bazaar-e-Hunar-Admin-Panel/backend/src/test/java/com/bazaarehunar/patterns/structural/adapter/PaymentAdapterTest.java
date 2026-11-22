package com.bazaarehunar.patterns.structural.adapter;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PaymentAdapterTest {
    @Test
    void easyPaisaAdapterConvertsToReceipt() {
        PaymentGateway gateway = new EasyPaisaGatewayAdapter(new LegacyEasyPaisaClient());
        PaymentReceipt ok = gateway.charge("03001234567", 1500);
        assertTrue(ok.success());
        assertEquals("EP-4567-150000", ok.reference());
        assertFalse(gateway.charge("123", 1500).success());
    }

    @Test
    void jazzCashAdapterConvertsStatusCodes() {
        PaymentGateway gateway = new JazzCashGatewayAdapter(new LegacyJazzCashSdk());
        PaymentReceipt ok = gateway.charge("03211234567", 200);
        assertTrue(ok.success());
        assertEquals("JC-4567-20000", ok.reference());
        PaymentReceipt bad = gateway.charge("03211234567", 0);
        assertFalse(bad.success());
        assertTrue(bad.message().contains("102"));
    }
}
