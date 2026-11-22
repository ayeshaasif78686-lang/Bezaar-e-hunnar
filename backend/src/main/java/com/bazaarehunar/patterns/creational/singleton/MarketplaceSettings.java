package com.bazaarehunar.patterns.creational.singleton;

/**
 * SINGLETON (creational).
 * One shared, thread-safe configuration object for the whole marketplace (currency, shipping fees and
 * thresholds). Every part of the backend that needs these values asks for the same instance, so the
 * numbers can never disagree between checkout, cart and shipping code.
 *
 * Uses the "initialization-on-demand holder" idiom: lazy, thread-safe and without explicit locking.
 */
public final class MarketplaceSettings {

    private static final class Holder {
        private static final MarketplaceSettings INSTANCE = new MarketplaceSettings();
    }

    public static final String CURRENCY = "PKR";

    private volatile int freeShippingThreshold;
    private volatile int standardShippingFee;
    private volatile int expressShippingFee;
    private volatile int bulkDiscountThreshold;

    private MarketplaceSettings() {
        resetToDefaults();
    }

    public static MarketplaceSettings getInstance() {
        return Holder.INSTANCE;
    }

    public String currency() { return CURRENCY; }
    public int freeShippingThreshold() { return freeShippingThreshold; }
    public int standardShippingFee() { return standardShippingFee; }
    public int expressShippingFee() { return expressShippingFee; }
    public int bulkDiscountThreshold() { return bulkDiscountThreshold; }

    public synchronized void updateShipping(int freeThreshold, int standardFee, int expressFee) {
        if (freeThreshold < 0 || standardFee < 0 || expressFee < standardFee) {
            throw new IllegalArgumentException("Invalid shipping settings");
        }
        this.freeShippingThreshold = freeThreshold;
        this.standardShippingFee = standardFee;
        this.expressShippingFee = expressFee;
    }

    public synchronized void resetToDefaults() {
        this.freeShippingThreshold = 5000;
        this.standardShippingFee = 300;
        this.expressShippingFee = 600;
        this.bulkDiscountThreshold = 10000;
    }
}
