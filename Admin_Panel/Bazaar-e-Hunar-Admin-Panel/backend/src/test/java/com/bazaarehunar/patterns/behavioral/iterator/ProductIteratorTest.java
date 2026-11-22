package com.bazaarehunar.patterns.behavioral.iterator;

import com.bazaarehunar.model.CatalogProduct;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class ProductIteratorTest {
    @Test
    void customIteratorTraversesCatalog() {
        CatalogProduct a = new CatalogProduct(); a.setName("A");
        CatalogProduct b = new CatalogProduct(); b.setName("B");
        ProductIterator iterator = new ProductCatalog(List.of(a, b)).iterator();
        assertTrue(iterator.hasNext());
        assertEquals("A", iterator.next().getName());
        assertEquals("B", iterator.next().getName());
        assertFalse(iterator.hasNext());
    }
}
