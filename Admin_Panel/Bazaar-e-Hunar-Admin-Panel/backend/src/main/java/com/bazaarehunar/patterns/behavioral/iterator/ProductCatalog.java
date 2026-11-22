package com.bazaarehunar.patterns.behavioral.iterator;

import com.bazaarehunar.model.CatalogProduct;
import java.util.ArrayList;
import java.util.List;

public class ProductCatalog {
    private final List<CatalogProduct> products = new ArrayList<>();
    public ProductCatalog(List<CatalogProduct> products) { this.products.addAll(products); }
    public ProductIterator iterator() { return new ProductIterator(List.copyOf(products)); }
    public int size() { return products.size(); }
}
