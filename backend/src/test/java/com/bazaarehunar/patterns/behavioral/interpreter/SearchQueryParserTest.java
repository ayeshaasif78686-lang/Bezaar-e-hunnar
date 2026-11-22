package com.bazaarehunar.patterns.behavioral.interpreter;

import com.bazaarehunar.model.CatalogProduct;
import com.bazaarehunar.patterns.creational.builder.CatalogProductBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SearchQueryParserTest {
    private final SearchQueryParser parser = new SearchQueryParser();

    private CatalogProduct product(String name, String category, int price, double rating, String region) {
        return new CatalogProductBuilder().name(name).category(category).price(price).rating(rating)
                .artisan("Ali", "a1").region(region).build();
    }

    private final CatalogProduct vase = product("Blue Pottery Vase", "Pottery", 3000, 4.7, "Multan");
    private final CatalogProduct shawl = product("Ajrak Shawl", "Textiles", 6500, 4.2, "Sindh");

    @Test
    void simpleConditions() {
        assertTrue(parser.parse("category:pottery").interpret(vase));
        assertFalse(parser.parse("category:pottery").interpret(shawl));
        assertTrue(parser.parse("price<5000").interpret(vase));
        assertTrue(parser.parse("rating>=4.5").interpret(vase));
    }

    @Test
    void andOrNotAndParentheses() {
        Expression query = parser.parse("category:pottery AND (price<5000 OR rating>=4.9) AND NOT region:Sindh");
        assertTrue(query.interpret(vase));
        assertFalse(query.interpret(shawl));
        assertTrue(parser.parse("category:pottery OR category:textiles").interpret(shawl));
    }

    @Test
    void implicitAndAndQuotedValues() {
        assertTrue(parser.parse("name:\"blue pottery\" price<4000").interpret(vase));
        assertFalse(parser.parse("name:\"blue pottery\" price>4000").interpret(vase));
    }

    @Test
    void badQueriesAreRejected() {
        assertThrows(IllegalArgumentException.class, () -> parser.parse(""));
        assertThrows(IllegalArgumentException.class, () -> parser.parse("price<abc"));
        assertThrows(IllegalArgumentException.class, () -> parser.parse("(price<5"));
        assertThrows(IllegalArgumentException.class, () -> parser.parse("colour:red"));
        assertThrows(IllegalArgumentException.class, () -> parser.parse("category:pottery AND"));
    }
}
