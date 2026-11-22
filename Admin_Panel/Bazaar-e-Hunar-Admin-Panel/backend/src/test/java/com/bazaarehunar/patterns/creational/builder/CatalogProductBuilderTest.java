package com.bazaarehunar.patterns.creational.builder;

import com.bazaarehunar.model.CatalogProduct;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CatalogProductBuilderTest {
    @Test
    void buildsProductWithDefaults() {
        CatalogProduct p = new CatalogProductBuilder().id("p1").name("Ajrak Shawl").category("Textiles")
                .price(4500).artisan("Ayesha", "a1").build();
        assertEquals("Pakistan", p.getRegion());
        assertEquals(4500, p.getPrice());
        assertFalse(p.isCustomizable());
    }

    @Test
    void validatesRequiredFields() {
        assertThrows(IllegalStateException.class, () -> new CatalogProductBuilder().name("x").build());
    }

    @Test
    void directorBuildsStandardRecipes() {
        CatalogProductDirector director = new CatalogProductDirector(new CatalogProductBuilder());
        CatalogProduct custom = director.constructCustomizable("c1", "Custom Vase", "Pottery", 3000, "Bilal", "b1");
        assertTrue(custom.isCustomizable());
        assertEquals("Custom Order", custom.getBadge());
        CatalogProduct heritage = new CatalogProductDirector(new CatalogProductBuilder())
                .constructHeritage("h1", "Rilli", "Textiles", 9000, "Sana", "s1", "Sindh");
        assertEquals("Sindh", heritage.getRegion());
        assertEquals(5.0, heritage.getRating());
    }
}
