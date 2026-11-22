package com.bazaarehunar.patterns.behavioral.visitor;

public interface CartLineVisitor {
    void visit(ReadyMadeLine line);
    void visit(CustomMadeLine line);
    void visit(GiftBoxLine line);
}
