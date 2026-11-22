package com.bazaarehunar.patterns.behavioral.templatemethod;

import com.bazaarehunar.model.CatalogProduct;

/** Same text layout, but only products that carry a badge (overrides the filter hook and header). */
public class FeaturedCatalogReport extends TextCatalogReport {
    @Override protected String header() { return "=== Featured Products ==="; }
    @Override protected boolean include(CatalogProduct p) { return p.getBadge() != null && !p.getBadge().isBlank(); }
}
