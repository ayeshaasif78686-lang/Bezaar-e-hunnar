package com.bazaarehunar.patterns.creational.abstractfactory;

/** Abstract product A of the checkout family: how an order travels. */
public interface ShippingOption {
    String carrier();
    int fee();
    String estimate();
}
