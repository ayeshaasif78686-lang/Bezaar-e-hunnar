package com.bazaarehunar.patterns.behavioral.memento;

import java.util.ArrayDeque;
import java.util.Deque;

public class CartHistory {
    private final Deque<CartMemento> history = new ArrayDeque<>();
    public void push(CartMemento memento) { history.push(memento); }
    public CartMemento pop() { return history.pop(); }
    public int size() { return history.size(); }
}
