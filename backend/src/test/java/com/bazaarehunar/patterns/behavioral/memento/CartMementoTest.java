package com.bazaarehunar.patterns.behavioral.memento;

import com.bazaarehunar.patterns.behavioral.command.CartReceiver;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CartMementoTest {
    @Test
    void cartCanRestorePreviousSnapshot() {
        CartReceiver cart = new CartReceiver();
        cart.add("rug", 1);
        CartOriginator originator = new CartOriginator(cart);
        CartMemento saved = originator.save();
        cart.add("vase", 2);
        originator.restore(saved);
        assertEquals(1, cart.quantityOf("rug"));
        assertEquals(0, cart.quantityOf("vase"));
    }
}
