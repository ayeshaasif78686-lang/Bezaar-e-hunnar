package com.bazaarehunar.patterns.creational.factorymethod;

import com.bazaarehunar.model.CatalogProduct;
import com.bazaarehunar.patterns.structural.flyweight.CraftStyleFlyweight;

public abstract class ProductCreator {
    public CatalogProduct create(String id, String name, String category, int price, double rating, int reviews,
                                 String badge, String image, String artisan, String artisanId,
                                 String description, CraftStyleFlyweight style) {
        CatalogProduct product = buildProduct();
        product.setId(id);
        product.setName(name);
        product.setCategory(category);
        product.setPrice(price);
        product.setRating(rating);
        product.setReviews(reviews);
        product.setBadge(badge);
        product.setImage(image);
        product.setArtisan(artisan);
        product.setArtisanId(artisanId);
        product.setDescription(description);
        product.setRegion(style.region());
        product.setMaterial(style.material());
        product.setStyleKey(style.key());
        return product;
    }

    protected abstract CatalogProduct buildProduct();
}
