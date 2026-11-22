package com.bazaarehunar.patterns.behavioral.visitor;

public record GiftBoxLine(String name, int price, int quantity) implements CartLine {
    @Override public void accept(CartLineVisitor visitor) { visitor.visit(this); }
}
