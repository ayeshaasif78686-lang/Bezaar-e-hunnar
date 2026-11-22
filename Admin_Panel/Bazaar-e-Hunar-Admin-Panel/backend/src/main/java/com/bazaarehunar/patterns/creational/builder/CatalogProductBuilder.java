package com.bazaarehunar.patterns.creational.builder;

import com.bazaarehunar.model.CatalogProduct;

/**
 * BUILDER (creational).
 * CatalogProduct has 15 fields, so a constructor call is unreadable and error-prone. The builder sets them
 * step by step (fluent API), applies safe defaults and validates the required ones in {@link #build()}.
 */
public class CatalogProductBuilder {
    private String id;
    private String name;
    private String category;
    private int price = -1;
    private double rating;
    private int reviews;
    private String badge;
    private String image;
    private String artisan;
    private String artisanId;
    private String region = "Pakistan";
    private String material = "Handmade";
    private String description = "";
    private boolean customizable;
    private String styleKey;

    public CatalogProductBuilder id(String id) { this.id = id; return this; }
    public CatalogProductBuilder name(String name) { this.name = name; return this; }
    public CatalogProductBuilder category(String category) { this.category = category; return this; }
    public CatalogProductBuilder price(int price) { this.price = price; return this; }
    public CatalogProductBuilder rating(double rating) { this.rating = rating; return this; }
    public CatalogProductBuilder reviews(int reviews) { this.reviews = reviews; return this; }
    public CatalogProductBuilder badge(String badge) { this.badge = badge; return this; }
    public CatalogProductBuilder image(String image) { this.image = image; return this; }
    public CatalogProductBuilder artisan(String artisan, String artisanId) {
        this.artisan = artisan;
        this.artisanId = artisanId;
        return this;
    }
    public CatalogProductBuilder region(String region) { this.region = region; return this; }
    public CatalogProductBuilder material(String material) { this.material = material; return this; }
    public CatalogProductBuilder description(String description) { this.description = description; return this; }
    public CatalogProductBuilder customizable(boolean customizable) { this.customizable = customizable; return this; }
    public CatalogProductBuilder styleKey(String styleKey) { this.styleKey = styleKey; return this; }

    public CatalogProduct build() {
        if (isBlank(name)) throw new IllegalStateException("Product name is required");
        if (isBlank(category)) throw new IllegalStateException("Product category is required");
        if (isBlank(artisan)) throw new IllegalStateException("Product artisan is required");
        if (price < 0) throw new IllegalStateException("Product price is required and cannot be negative");
        if (rating < 0 || rating > 5) throw new IllegalStateException("Rating must be between 0 and 5");

        return new CatalogProduct(id, name, category, price, rating, reviews, badge, image, artisan, artisanId,
                region, material, description, customizable, styleKey);
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
