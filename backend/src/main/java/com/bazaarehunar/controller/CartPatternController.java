package com.bazaarehunar.controller;

import com.bazaarehunar.patterns.behavioral.command.AddToCartCommand;
import com.bazaarehunar.patterns.behavioral.command.CartReceiver;
import com.bazaarehunar.patterns.behavioral.command.RemoveFromCartCommand;
import com.bazaarehunar.patterns.behavioral.memento.CartHistory;
import com.bazaarehunar.patterns.behavioral.memento.CartOriginator;
import com.bazaarehunar.patterns.behavioral.strategy.BulkPricingStrategy;
import com.bazaarehunar.patterns.behavioral.strategy.DiscountPricingStrategy;
import com.bazaarehunar.patterns.behavioral.strategy.PricingStrategy;
import com.bazaarehunar.patterns.behavioral.strategy.RegularPricingStrategy;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class CartPatternController {

    @PostMapping("/cart/preview")
    public Map<String, Object> preview(@RequestBody Map<String, Object> request) {
        int subtotal = number(request.get("subtotal"));
        String coupon = String.valueOf(request.getOrDefault("coupon", "")).trim().toUpperCase();
        PricingStrategy strategy;
        if (coupon.equals("HUNAR10") || coupon.equals("WELCOME10")) {
            strategy = new DiscountPricingStrategy(10);
        } else if (subtotal >= 10000) {
            strategy = new BulkPricingStrategy();
        } else {
            strategy = new RegularPricingStrategy();
        }
        int afterPricing = strategy.calculate(subtotal);
        int shipping = subtotal == 0 || subtotal >= 5000 ? 0 : 300;
        return Map.of(
                "subtotal", subtotal,
                "strategy", strategy.name(),
                "discount", subtotal - afterPricing,
                "shipping", shipping,
                "total", afterPricing + shipping
        );
    }

    @GetMapping("/patterns/cart-demo")
    public Map<String, Object> demo() {
        CartReceiver cart = new CartReceiver();
        CartOriginator originator = new CartOriginator(cart);
        CartHistory history = new CartHistory();

        AddToCartCommand addRug = new AddToCartCommand(cart, "handwoven-rug", 1);
        addRug.execute();
        history.push(originator.save());

        AddToCartCommand addVase = new AddToCartCommand(cart, "blue-pottery-vase", 2);
        addVase.execute();
        Map<String, Integer> beforeRemove = new LinkedHashMap<>(cart.snapshot());

        RemoveFromCartCommand removeRug = new RemoveFromCartCommand(cart, "handwoven-rug");
        removeRug.execute();
        Map<String, Integer> afterRemove = new LinkedHashMap<>(cart.snapshot());
        originator.restore(history.pop());

        return Map.of(
                "command", Map.of("beforeRemove", beforeRemove, "afterRemove", afterRemove),
                "mementoRestored", cart.snapshot(),
                "strategyExample", new DiscountPricingStrategy(10).calculate(10000)
        );
    }

    private int number(Object value) {
        if (value instanceof Number number) return number.intValue();
        try { return Integer.parseInt(String.valueOf(value)); }
        catch (Exception ex) { return 0; }
    }
}
