package com.bazaarehunar.patterns.creational.prototype;

import com.bazaarehunar.model.CatalogProduct;
import com.bazaarehunar.patterns.creational.builder.CatalogProductBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProductTemplateTest {
    private CatalogProduct base() {
        return new CatalogProductBuilder().id("shawl-1").name("Ajrak Shawl").category("Textiles").price(4500)
                .rating(4.8).reviews(30).artisan("Ayesha", "a1").region("Sindh").material("Cotton").build();
    }

    @Test
    void cloneKeepsTemplateDataButGetsNewIdentity() {
        ProductTemplateRegistry registry = new ProductTemplateRegistry();
        registry.register("shawl", base());
        CatalogProduct clone = registry.create("shawl", "shawl-2", "Ajrak Shawl (Red)");
        assertEquals("shawl-2", clone.getId());
        assertEquals("Sindh", clone.getRegion());
        assertEquals(4500, clone.getPrice());
        assertEquals(0, clone.getReviews());
    }

    @Test
    void editingCloneDoesNotChangeTemplate() {
        ProductTemplate template = new ProductTemplate(base());
        CatalogProduct clone = template.instantiate("x", "X");
        clone.setPrice(1);
        assertEquals(4500, template.product().getPrice());
        assertNotSame(template, template.copy());
    }

    @Test
    void unknownTemplateFails() {
        assertThrows(IllegalArgumentException.class, () -> new ProductTemplateRegistry().create("nope", "a", "b"));
    }
}
