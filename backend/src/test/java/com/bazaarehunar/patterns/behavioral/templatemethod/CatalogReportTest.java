package com.bazaarehunar.patterns.behavioral.templatemethod;

import com.bazaarehunar.model.CatalogProduct;
import com.bazaarehunar.patterns.creational.builder.CatalogProductBuilder;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CatalogReportTest {
    private final List<CatalogProduct> products = List.of(
            new CatalogProductBuilder().id("1").name("Vase \"Blue\"").category("Pottery").price(3000).artisan("Ali", "a").badge("Handmade").build(),
            new CatalogProductBuilder().id("2").name("Shawl").category("Textiles").price(4500).artisan("Sana", "s").build());

    @Test
    void csvReportFollowsTemplate() {
        String[] lines = new CsvCatalogReport().generate(products).split("\n");
        assertEquals("id,name,category,price,artisan", lines[0]);
        assertEquals("1,\"Vase \"\"Blue\"\"\",\"Pottery\",3000,\"Ali\"", lines[1]);
        assertEquals(3, lines.length);
    }

    @Test
    void textReportAddsFooterHook() {
        String report = new TextCatalogReport().generate(products);
        assertTrue(report.contains("Items: 2, combined value: PKR 7500"));
    }

    @Test
    void featuredReportOverridesFilterHook() {
        String report = new FeaturedCatalogReport().generate(products);
        assertTrue(report.startsWith("=== Featured Products ==="));
        assertTrue(report.contains("Items: 1"));
        assertFalse(report.contains("Shawl"));
    }
}
