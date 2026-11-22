package com.bazaarehunar.patterns.structural.decorator;

public class FestivalDiscountDecorator extends PriceDecorator {
    private final int percent;

    public FestivalDiscountDecorator(PricedItem inner, int percent) {
        super(inner);
        if (percent < 0 || percent > 90) throw new IllegalArgumentException("Discount must be 0-90%");
        this.percent = percent;
    }

    @Override public int price() { return inner.price() - Math.round(inner.price() * percent / 100f); }
    @Override public String description() { return inner.description() + " - " + percent + "% festival offer"; }
}
