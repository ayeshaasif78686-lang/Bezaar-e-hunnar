package com.bazaarehunar.patterns.behavioral.visitor;

public record ReadyMadeLine(String name, int unitPrice, int quantity) implements CartLine {
    @Override public void accept(CartLineVisitor visitor) { visitor.visit(this); }
}
