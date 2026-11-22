package com.bazaarehunar.patterns.behavioral.memento;

import com.bazaarehunar.patterns.behavioral.command.CartReceiver;
import java.util.LinkedHashMap;

public class CartOriginator {
    private final CartReceiver receiver;
    public CartOriginator(CartReceiver receiver) { this.receiver = receiver; }
    public CartMemento save() { return new CartMemento(new LinkedHashMap<>(receiver.snapshot())); }
    public void restore(CartMemento memento) { receiver.restore(memento.quantities()); }
}
