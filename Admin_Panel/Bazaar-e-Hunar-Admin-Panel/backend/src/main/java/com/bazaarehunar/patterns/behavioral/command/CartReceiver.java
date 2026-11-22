package com.bazaarehunar.patterns.behavioral.command;

import java.util.LinkedHashMap;
import java.util.Map;

public class CartReceiver {
    private final Map<String, Integer> quantities = new LinkedHashMap<>();
    public void add(String productId, int quantity) { quantities.put(productId, quantities.getOrDefault(productId, 0) + quantity); }
    public void remove(String productId) { quantities.remove(productId); }
    public int quantityOf(String productId) { return quantities.getOrDefault(productId, 0); }
    public Map<String, Integer> snapshot() { return new LinkedHashMap<>(quantities); }
    public void restore(Map<String, Integer> state) { quantities.clear(); quantities.putAll(state); }
}
