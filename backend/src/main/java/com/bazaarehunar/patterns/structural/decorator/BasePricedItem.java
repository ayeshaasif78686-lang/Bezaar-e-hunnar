package com.bazaarehunar.patterns.structural.decorator;

/** Concrete component: a plain product with its catalog price. */
public class BasePricedItem implements PricedItem {
    private final String name;
    private final int price;

    public BasePricedItem(String name, int price) {
        this.name = name;
        this.price = price;
    }

    @Override public int price() { return price; }
    @Override public String description() { return name; }
}
