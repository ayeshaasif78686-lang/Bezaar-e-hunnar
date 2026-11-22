package com.bazaarehunar.patterns.behavioral.command;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CartCommandTest {
    @Test
    void commandCanExecuteAndUndo() {
        CartReceiver cart = new CartReceiver();
        Command add = new AddToCartCommand(cart, "rug", 2);
        add.execute();
        assertEquals(2, cart.quantityOf("rug"));
        add.undo();
        assertEquals(0, cart.quantityOf("rug"));
    }
}
