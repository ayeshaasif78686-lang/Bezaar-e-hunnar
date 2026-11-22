package com.bazaarehunar.patterns.structural.facade;

import com.bazaarehunar.patterns.creational.abstractfactory.CheckoutFactory;
import com.bazaarehunar.patterns.creational.singleton.MarketplaceSettings;
import com.bazaarehunar.patterns.structural.adapter.EasyPaisaGatewayAdapter;
import com.bazaarehunar.patterns.structural.adapter.LegacyEasyPaisaClient;
import com.bazaarehunar.patterns.structural.bridge.SmsChannel;
import com.bazaarehunar.patterns.structural.decorator.BasePricedItem;
import com.bazaarehunar.patterns.structural.decorator.PricedItem;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CheckoutFacadeTest {
    private final CheckoutFacade facade = new CheckoutFacade(CheckoutFactory.forCountry("Pakistan"),
            new EasyPaisaGatewayAdapter(new LegacyEasyPaisaClient()), new SmsChannel());

    @AfterEach
    void reset() { MarketplaceSettings.getInstance().resetToDefaults(); }

    @Test
    void oneCallRunsWholeCheckout() {
        List<PricedItem> items = List.of(new BasePricedItem("Rug", 4000));
        CheckoutResult result = facade.checkout("BEH1", "03001234567", items, "HUNAR10", false);
        assertTrue(result.success());
        assertEquals(4000, result.subtotal());
        assertEquals(400, result.discount());
        assertEquals(300, result.shipping());
        assertEquals(3900, result.total());
        assertNotNull(result.notification());
    }

    @Test
    void freeShippingAndBulkPricing() {
        CheckoutResult result = facade.checkout("BEH2", "03001234567", List.of(new BasePricedItem("Set", 10000)), "", true);
        assertEquals("BULK", result.pricingStrategy());
        assertEquals(0, result.shipping());
        assertEquals(9200, result.total());
    }

    @Test
    void paymentFailureStopsBeforeNotification() {
        CheckoutResult result = facade.checkout("BEH3", "bad", List.of(new BasePricedItem("Cup", 500)), "", false);
        assertFalse(result.success());
        assertNull(result.notification());
    }

    @Test
    void emptyCartFails() {
        assertFalse(facade.checkout("BEH4", "03001234567", List.of(), "", false).success());
    }
}
