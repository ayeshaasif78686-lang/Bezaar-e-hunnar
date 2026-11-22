package com.bazaarehunar.patterns.behavioral.interpreter;

import com.bazaarehunar.model.CatalogProduct;

/** Non-terminal expression: both sides must match. */
public class AndExpression implements Expression {
    private final Expression left;
    private final Expression right;
    public AndExpression(Expression left, Expression right) { this.left = left; this.right = right; }
    @Override public boolean interpret(CatalogProduct p) { return left.interpret(p) && right.interpret(p); }
}
