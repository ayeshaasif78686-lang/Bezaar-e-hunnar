package com.bazaarehunar.patterns.behavioral.visitor;

/** Estimated days before the order can ship = the slowest line (ready-made 1 day, gift box 1, custom = its own). */
public class PreparationTimeVisitor implements CartLineVisitor {
    private int days;

    @Override public void visit(ReadyMadeLine line) { days = Math.max(days, 1); }
    @Override public void visit(CustomMadeLine line) { days = Math.max(days, line.productionDays()); }
    @Override public void visit(GiftBoxLine line) { days = Math.max(days, 1); }

    public int days() { return days; }
}
