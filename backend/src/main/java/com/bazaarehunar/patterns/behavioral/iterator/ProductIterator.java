package com.bazaarehunar.patterns.behavioral.iterator;

import com.bazaarehunar.model.CatalogProduct;
import java.util.Iterator;
import java.util.List;

public class ProductIterator implements Iterator<CatalogProduct> {
    private final List<CatalogProduct> products;
    private int index;
    public ProductIterator(List<CatalogProduct> products) { this.products = products; }
    @Override public boolean hasNext() { return index < products.size(); }
    @Override public CatalogProduct next() { return products.get(index++); }
}
