package com.bazaarehunar.service;

import com.bazaarehunar.model.StoreSettings;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

/** Store settings edited in the admin panel (General, Store Details, Users & Access, Payment, Notifications). */
@Service
public class StoreSettingsService {
    private static final Map<String, Object> DEFAULTS = new LinkedHashMap<>();
    static {
        // General
        DEFAULTS.put("storeName", "Bazaar-e-Hunar");
        DEFAULTS.put("storeEmail", "admin@bazaar-e-hunar.com");
        DEFAULTS.put("currency", "PKR");
        // Store details
        DEFAULTS.put("storePhone", "");
        DEFAULTS.put("storeAddress", "");
        DEFAULTS.put("storeCity", "");
        DEFAULTS.put("storeCountry", "Pakistan");
        DEFAULTS.put("storeTagline", "Celebrating Our Heritage");
        // Users & access
        DEFAULTS.put("allowRegistration", true);
        DEFAULTS.put("allowSellerSignup", true);
        DEFAULTS.put("requireEmailVerification", false);
        // Payment
        DEFAULTS.put("payCod", true);
        DEFAULTS.put("payJazzCash", true);
        DEFAULTS.put("payEasyPaisa", true);
        DEFAULTS.put("payCard", true);
        // Notifications
        DEFAULTS.put("notifyNewOrder", true);
        DEFAULTS.put("notifyLowStock", true);
        DEFAULTS.put("lowStockThreshold", 5);
        DEFAULTS.put("notifyNewCustomer", false);
    }

    private final MongoTemplate mongoTemplate;

    public StoreSettingsService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public Map<String, Object> get() {
        Map<String, Object> result = new LinkedHashMap<>(DEFAULTS);
        StoreSettings stored = mongoTemplate.findById("store", StoreSettings.class);
        if (stored != null && stored.getValues() != null) {
            stored.getValues().forEach((key, value) -> {
                if (DEFAULTS.containsKey(key) && value != null) result.put(key, value);
            });
        }
        return result;
    }

    public boolean getBoolean(String key) {
        try {
            return Boolean.TRUE.equals(get().get(key));
        } catch (RuntimeException ex) {
            return Boolean.TRUE.equals(DEFAULTS.get(key));
        }
    }

    public int getInt(String key) {
        Object value = get().get(key);
        return value instanceof Number n ? n.intValue() : ((Number) DEFAULTS.get(key)).intValue();
    }

    /** Merges the given values (unknown keys ignored, wrong types rejected) and saves them. */
    public Map<String, Object> update(Map<String, Object> input) {
        Map<String, Object> merged = get();
        for (Map.Entry<String, Object> entry : input.entrySet()) {
            Object fallback = DEFAULTS.get(entry.getKey());
            if (fallback == null) continue;
            Object value = entry.getValue();
            if (fallback instanceof Boolean) {
                if (!(value instanceof Boolean)) throw new IllegalArgumentException(entry.getKey() + " must be true or false");
                merged.put(entry.getKey(), value);
            } else if (fallback instanceof Integer) {
                int number;
                try {
                    number = value instanceof Number n ? n.intValue() : Integer.parseInt(String.valueOf(value).trim());
                } catch (NumberFormatException ex) {
                    throw new IllegalArgumentException(entry.getKey() + " must be a number");
                }
                if (number < 0 || number > 100000) throw new IllegalArgumentException(entry.getKey() + " is out of range");
                merged.put(entry.getKey(), number);
            } else {
                String text = value == null ? "" : String.valueOf(value).trim();
                if (text.length() > 200) throw new IllegalArgumentException(entry.getKey() + " is too long");
                merged.put(entry.getKey(), text);
            }
        }
        if (String.valueOf(merged.get("storeName")).isBlank()) throw new IllegalArgumentException("Store name is required");
        String email = String.valueOf(merged.get("storeEmail"));
        if (!email.isBlank() && !email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            throw new IllegalArgumentException("Enter a valid store email");
        }
        String currency = String.valueOf(merged.get("currency"));
        if (!java.util.Set.of("PKR", "USD", "EUR", "GBP", "AED").contains(currency)) {
            throw new IllegalArgumentException("Unsupported currency");
        }
        StoreSettings doc = new StoreSettings();
        doc.setValues(merged);
        mongoTemplate.save(doc);
        return merged;
    }
}
