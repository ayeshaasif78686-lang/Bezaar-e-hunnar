package com.bazaarehunar.patterns.structural.decorator;

/**
 * DECORATOR (structural).
 * Base decorator: wraps another {@link PricedItem} and adds extra cost/description. Options such as gift
 * wrap, embroidery or a festival discount can be stacked in any combination at runtime, with no
 * subclass-per-combination explosion.
 */
public abstract class PriceDecorator implements PricedItem {
    protected final PricedItem inner;

    protected PriceDecorator(PricedItem inner) {
        this.inner = inner;
    }

    @Override public int price() { return inner.price(); }
    @Override public String description() { return inner.description(); }
}
