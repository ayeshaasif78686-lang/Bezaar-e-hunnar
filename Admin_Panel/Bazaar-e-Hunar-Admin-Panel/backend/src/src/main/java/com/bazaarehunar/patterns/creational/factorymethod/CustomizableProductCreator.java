package com.bazaarehunar.patterns.creational.factorymethod;

import com.bazaarehunar.model.CatalogProduct;

public class CustomizableProductCreator extends ProductCreator {
    @Override
    protected CatalogProduct buildProduct() {
        CatalogProduct product = new CatalogProduct();
        product.setCustomizable(true);
        return product;
    }
}
