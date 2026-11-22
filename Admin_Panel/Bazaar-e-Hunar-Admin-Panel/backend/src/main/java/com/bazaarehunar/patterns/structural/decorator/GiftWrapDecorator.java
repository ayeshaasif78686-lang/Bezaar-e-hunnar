package com.bazaarehunar.patterns.structural.decorator;

public class GiftWrapDecorator extends PriceDecorator {
    public static final int FEE = 250;
    public GiftWrapDecorator(PricedItem inner) { super(inner); }
    @Override public int price() { return inner.price() + FEE; }
    @Override public String description() { return inner.description() + " + gift wrap"; }
}
