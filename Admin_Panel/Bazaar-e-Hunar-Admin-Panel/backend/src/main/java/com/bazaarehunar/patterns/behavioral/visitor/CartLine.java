package com.bazaarehunar.patterns.behavioral.visitor;

/**
 * VISITOR (behavioral).
 * Element side: the different kinds of cart lines. New operations (totals, receipts, preparation time ...)
 * are added as new {@link CartLineVisitor}s without touching these classes.
 */
public interface CartLine {
    void accept(CartLineVisitor visitor);
}
