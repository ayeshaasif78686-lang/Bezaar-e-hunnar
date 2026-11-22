package com.bazaarehunar.patterns.creational.builder;

import com.bazaarehunar.model.CatalogProduct;

/** Director of the Builder pattern: knows the standard recipes for common kinds of listings. */
public class CatalogProductDirector {
    private final CatalogProductBuilder builder;

    public CatalogProductDirector(CatalogProductBuilder builder) {
        this.builder = builder;
    }

    /** A ready-made, non-customizable listing with the "Handmade" badge. */
    public CatalogProduct constructHandmade(String id, String name, String category, int price,
                                            String artisan, String artisanId) {
        return builder.id(id).name(name).category(category).price(price)
                .artisan(artisan, artisanId).badge("Handmade").customizable(false).build();
    }

    /** A made-to-order listing that buyers can personalise. */
    public CatalogProduct constructCustomizable(String id, String name, String category, int price,
                                                String artisan, String artisanId) {
        return builder.id(id).name(name).category(category).price(price)
                .artisan(artisan, artisanId).badge("Custom Order").customizable(true).build();
    }

    /** A highlighted heritage piece with a top rating. */
    public CatalogProduct constructHeritage(String id, String name, String category, int price,
                                            String artisan, String artisanId, String region) {
        return builder.id(id).name(name).category(category).price(price)
                .artisan(artisan, artisanId).region(region).badge("Heritage").rating(5.0).build();
    }
}
