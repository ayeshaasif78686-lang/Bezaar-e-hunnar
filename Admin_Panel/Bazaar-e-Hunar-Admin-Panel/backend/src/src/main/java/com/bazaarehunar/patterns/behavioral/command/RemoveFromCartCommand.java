package com.bazaarehunar.patterns.behavioral.command;

public class RemoveFromCartCommand implements Command {
    private final CartReceiver cart;
    private final String productId;
    private int removedQuantity;
    public RemoveFromCartCommand(CartReceiver cart, String productId) { this.cart = cart; this.productId = productId; }
    @Override public void execute() { removedQuantity = cart.quantityOf(productId); cart.remove(productId); }
    @Override public void undo() { if (removedQuantity > 0) cart.add(productId, removedQuantity); }
}
