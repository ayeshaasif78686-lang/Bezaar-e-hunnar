package com.bazaarehunar.patterns.behavioral.templatemethod;

import com.bazaarehunar.model.CatalogProduct;

import java.util.List;

public class TextCatalogReport extends CatalogReport {
    @Override protected String header() { return "=== Bazaar-e-Hunar Catalog ==="; }
    @Override protected String row(CatalogProduct p) {
        return "- " + p.getName() + " (" + p.getCategory() + ") PKR " + p.getPrice() + " by " + p.getArtisan();
    }
    @Override protected String footer(List<CatalogProduct> selected) {
        int total = selected.stream().mapToInt(CatalogProduct::getPrice).sum();
        return "Items: " + selected.size() + ", combined value: PKR " + total;
    }
}
