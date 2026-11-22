package com.bazaarehunar.patterns.behavioral.templatemethod;

import com.bazaarehunar.model.CatalogProduct;

public class CsvCatalogReport extends CatalogReport {
    @Override protected String header() { return "id,name,category,price,artisan"; }
    @Override protected String row(CatalogProduct p) {
        return String.join(",", p.getId(), quote(p.getName()), quote(p.getCategory()),
                String.valueOf(p.getPrice()), quote(p.getArtisan()));
    }
    private static String quote(String value) {
        return "\"" + (value == null ? "" : value.replace("\"", "\"\"")) + "\"";
    }
}
