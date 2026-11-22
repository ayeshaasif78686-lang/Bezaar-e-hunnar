package com.bazaarehunar.patterns.behavioral.visitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ReceiptVisitor implements CartLineVisitor {
    private final List<String> lines = new ArrayList<>();

    @Override public void visit(ReadyMadeLine l) {
        lines.add(l.quantity() + " x " + l.name() + " @ " + l.unitPrice());
    }
    @Override public void visit(CustomMadeLine l) {
        lines.add(l.quantity() + " x " + l.name() + " (custom, +" + l.customizationFee() + ") @ " + l.unitPrice());
    }
    @Override public void visit(GiftBoxLine l) {
        lines.add(l.quantity() + " x gift box: " + l.name() + " @ " + l.price());
    }

    public List<String> lines() { return Collections.unmodifiableList(lines); }
}
