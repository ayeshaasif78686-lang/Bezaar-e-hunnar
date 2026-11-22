package com.bazaarehunar.patterns.creational.singleton;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MarketplaceSettingsTest {
    @AfterEach
    void reset() { MarketplaceSettings.getInstance().resetToDefaults(); }

    @Test
    void alwaysReturnsTheSameInstance() {
        assertSame(MarketplaceSettings.getInstance(), MarketplaceSettings.getInstance());
    }

    @Test
    void changesAreVisibleEverywhere() {
        MarketplaceSettings.getInstance().updateShipping(8000, 350, 700);
        assertEquals(350, MarketplaceSettings.getInstance().standardShippingFee());
        assertEquals(8000, MarketplaceSettings.getInstance().freeShippingThreshold());
    }

    @Test
    void rejectsInvalidValues() {
        assertThrows(IllegalArgumentException.class, () -> MarketplaceSettings.getInstance().updateShipping(100, 500, 200));
    }
}
