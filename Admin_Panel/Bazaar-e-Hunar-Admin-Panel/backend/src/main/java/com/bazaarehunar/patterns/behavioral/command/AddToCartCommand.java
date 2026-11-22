package com.bazaarehunar.patterns.behavioral.command;

public class AddToCartCommand implements Command {
    private final CartReceiver cart;
    private final String productId;
    private final int quantity;
    public AddToCartCommand(CartReceiver cart, String productId, int quantity) {
        this.cart = cart; this.productId = productId; this.quantity = quantity;
    }
    @Override public void execute() { cart.add(productId, quantity); }
    @Override public void undo() {
        int remaining = cart.quantityOf(productId) - quantity;
        if (remaining <= 0) cart.remove(productId);
        else {
            cart.remove(productId);
            cart.add(productId, remaining);
        }
    }
}
