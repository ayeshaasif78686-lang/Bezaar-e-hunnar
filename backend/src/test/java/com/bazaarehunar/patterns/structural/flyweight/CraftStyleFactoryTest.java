package com.bazaarehunar.patterns.structural.flyweight;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CraftStyleFactoryTest {
    @Test
    void sameStyleIsShared() {
        CraftStyleFactory factory = new CraftStyleFactory();
        var one = factory.getStyle("Multan", "Clay", "Blue Pottery");
        var two = factory.getStyle("Multan", "Clay", "Blue Pottery");
        assertSame(one, two);
        assertEquals(1, factory.cachedStyleCount());
    }
}
