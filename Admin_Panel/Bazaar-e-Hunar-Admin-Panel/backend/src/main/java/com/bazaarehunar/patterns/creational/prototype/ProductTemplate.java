package com.bazaarehunar.patterns.creational.prototype;

import com.bazaarehunar.model.CatalogProduct;

/**
 * PROTOTYPE (creational).
 * A seller who lists many similar items (e.g. 20 colours of the same shawl) clones a template product and
 * only changes the id/name/price instead of filling in every field again. {@link #copy()} is a deep copy,
 * so editing the clone never changes the original template.
 */
public class ProductTemplate implements Prototype<ProductTemplate> {
    private final CatalogProduct product;

    public ProductTemplate(CatalogProduct product) {
        this.product = duplicate(product);
    }

    @Override
    public ProductTemplate copy() {
        return new ProductTemplate(product);
    }

    /** Returns a fresh CatalogProduct cloned from this template with a new identity. */
    public CatalogProduct instantiate(String newId, String newName) {
        CatalogProduct clone = duplicate(product);
        clone.setId(newId);
        clone.setName(newName);
        clone.setReviews(0);
        clone.setRating(0);
        return clone;
    }

    public CatalogProduct product() {
        return duplicate(product);
    }

    private static CatalogProduct duplicate(CatalogProduct source) {
        CatalogProduct clone = new CatalogProduct(source.getId(), source.getName(), source.getCategory(),
                source.getPrice(), source.getRating(), source.getReviews(), source.getBadge(), source.getImage(),
                source.getArtisan(), source.getArtisanId(), source.getRegion(), source.getMaterial(),
                source.getDescription(), source.isCustomizable(), source.getStyleKey());
        clone.setPosition(source.getPosition());
        clone.setStock(source.getStock());
        clone.setActive(source.isActive());
        return clone;
    }
}
