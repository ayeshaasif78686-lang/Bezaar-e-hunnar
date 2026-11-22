package com.bazaarehunar.patterns.behavioral.interpreter;

import com.bazaarehunar.model.CatalogProduct;

/** Non-terminal expression: negates another expression. */
public class NotExpression implements Expression {
    private final Expression inner;
    public NotExpression(Expression inner) { this.inner = inner; }
    @Override public boolean interpret(CatalogProduct p) { return !inner.interpret(p); }
}
