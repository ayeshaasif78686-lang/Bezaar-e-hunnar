package com.bazaarehunar.patterns.behavioral.interpreter;

import com.bazaarehunar.model.CatalogProduct;

import java.util.Locale;

/** Terminal expression: price/rating/reviews compared with a number using &lt; &lt;= &gt; &gt;= =. */
public class NumberComparisonExpression implements Expression {
    private final String field;
    private final String operator;
    private final double number;

    public NumberComparisonExpression(String field, String operator, double number) {
        this.field = field.toLowerCase(Locale.ROOT);
        this.operator = operator;
        this.number = number;
        if (!java.util.Set.of("price", "rating", "reviews").contains(this.field)) {
            throw new IllegalArgumentException("Unknown number field: " + field);
        }
    }

    @Override
    public boolean interpret(CatalogProduct p) {
        double actual = switch (field) {
            case "price" -> p.getPrice();
            case "rating" -> p.getRating();
            default -> p.getReviews();
        };
        return switch (operator) {
            case "<" -> actual < number;
            case "<=" -> actual <= number;
            case ">" -> actual > number;
            case ">=" -> actual >= number;
            case "=", ":" -> actual == number;
            default -> throw new IllegalArgumentException("Unknown operator: " + operator);
        };
    }
}
