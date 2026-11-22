package com.bazaarehunar.patterns.creational.factorymethod;

import com.bazaarehunar.model.CatalogProduct;

public class StandardProductCreator extends ProductCreator {
    @Override
    protected CatalogProduct buildProduct() {
        CatalogProduct product = new CatalogProduct();
        product.setCustomizable(false);
        return product;
    }
}
