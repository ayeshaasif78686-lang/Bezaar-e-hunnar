package com.bazaarehunar.patterns.creational.factorymethod;

import com.bazaarehunar.model.CatalogProduct;
import com.bazaarehunar.patterns.structural.flyweight.CraftStyleFactory;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ProductCreatorTest {
    @Test
    void factoryMethodCreatesCorrectProductKind() {
        var style = new CraftStyleFactory().getStyle("Multan", "Clay", "Blue Pottery");
        CatalogProduct standard = new StandardProductCreator().create("1", "Vase", "Pottery", 1000, 5, 1, "", "img", "Ustad", "u", "d", style);
        CatalogProduct custom = new CustomizableProductCreator().create("2", "Custom Vase", "Pottery", 1200, 5, 1, "", "img", "Ustad", "u", "d", style);
        assertFalse(standard.isCustomizable());
        assertTrue(custom.isCustomizable());
    }
}
