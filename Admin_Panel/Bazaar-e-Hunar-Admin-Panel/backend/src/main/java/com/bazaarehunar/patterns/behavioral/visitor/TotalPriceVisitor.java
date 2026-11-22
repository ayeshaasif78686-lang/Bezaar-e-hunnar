package com.bazaarehunar.patterns.behavioral.visitor;

public class TotalPriceVisitor implements CartLineVisitor {
    private int total;

    @Override public void visit(ReadyMadeLine line) { total += line.unitPrice() * line.quantity(); }
    @Override public void visit(CustomMadeLine line) { total += (line.unitPrice() + line.customizationFee()) * line.quantity(); }
    @Override public void visit(GiftBoxLine line) { total += line.price() * line.quantity(); }

    public int total() { return total; }
}
