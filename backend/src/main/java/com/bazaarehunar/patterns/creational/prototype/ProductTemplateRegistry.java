package com.bazaarehunar.patterns.creational.prototype;

import com.bazaarehunar.model.CatalogProduct;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/** Keeps named prototypes and hands out clones of them. */
public class ProductTemplateRegistry {
    private final Map<String, ProductTemplate> templates = new LinkedHashMap<>();

    public void register(String key, CatalogProduct product) {
        templates.put(key, new ProductTemplate(product));
    }

    public CatalogProduct create(String key, String newId, String newName) {
        ProductTemplate template = templates.get(key);
        if (template == null) {
            throw new IllegalArgumentException("No product template named " + key);
        }
        return template.copy().instantiate(newId, newName);
    }

    public Set<String> keys() {
        return templates.keySet();
    }
}
