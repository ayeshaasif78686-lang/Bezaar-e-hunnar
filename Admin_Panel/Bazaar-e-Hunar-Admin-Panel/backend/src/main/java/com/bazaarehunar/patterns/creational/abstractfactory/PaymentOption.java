package com.bazaarehunar.patterns.creational.abstractfactory;

/** Abstract product B of the checkout family: how the buyer pays. */
public interface PaymentOption {
    String name();
    String instructions();
    boolean acceptsOnline();
}
