package com.bazaarehunar.patterns.structural.composite;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CategoryCompositeTest {
    @Test
    void compositeAndLeafAreHandledUniformly() {
        CategoryComposite pottery = new CategoryComposite("Pottery").add(new CategoryLeaf("Vases")).add(new CategoryLeaf("Bowls"));
        assertEquals("Pottery", pottery.name());
        assertEquals(2, pottery.children().size());
        assertTrue(pottery.children().get(0).leaf());
    }
}
