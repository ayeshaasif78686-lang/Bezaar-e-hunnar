package com.bazaarehunar.patterns.behavioral.visitor;

/** Made-to-order item: has an extra customisation fee and a production time. */
public record CustomMadeLine(String name, int unitPrice, int quantity, int customizationFee, int productionDays)
        implements CartLine {
    @Override public void accept(CartLineVisitor visitor) { visitor.visit(this); }
}
