package com.bazaarehunar.patterns.behavioral.interpreter;

import com.bazaarehunar.model.CatalogProduct;

import java.util.Locale;

/** Terminal expression: text field contains value (case-insensitive). */
public class TextFieldExpression implements Expression {
    private final String field;
    private final String value;

    public TextFieldExpression(String field, String value) {
        this.field = field.toLowerCase(Locale.ROOT);
        this.value = value.toLowerCase(Locale.ROOT);
        if (!java.util.Set.of("name", "category", "region", "material", "artisan", "badge").contains(this.field)) {
            throw new IllegalArgumentException("Unknown text field: " + field);
        }
    }

    @Override
    public boolean interpret(CatalogProduct p) {
        String actual = switch (field) {
            case "name" -> p.getName();
            case "category" -> p.getCategory();
            case "region" -> p.getRegion();
            case "material" -> p.getMaterial();
            case "artisan" -> p.getArtisan();
            default -> p.getBadge();
        };
        return actual != null && actual.toLowerCase(Locale.ROOT).contains(value);
    }
}
