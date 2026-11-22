package com.bazaarehunar.patterns.behavioral.templatemethod;

import com.bazaarehunar.model.CatalogProduct;

import java.util.List;

/**
 * TEMPLATE METHOD (behavioral).
 * {@link #generate} fixes the skeleton of every catalog report (select -> header -> rows -> footer).
 * Subclasses fill in the steps that differ (format) and may override optional hooks (filter, footer),
 * but cannot change the order of the steps.
 */
public abstract class CatalogReport {

    public final String generate(List<CatalogProduct> products) {
        List<CatalogProduct> selected = products.stream().filter(this::include).toList();
        StringBuilder out = new StringBuilder();
        out.append(header()).append('\n');
        for (CatalogProduct product : selected) {
            out.append(row(product)).append('\n');
        }
        String footer = footer(selected);
        if (!footer.isEmpty()) out.append(footer).append('\n');
        return out.toString();
    }

    protected abstract String header();
    protected abstract String row(CatalogProduct product);

    /** Hook: which products belong in the report (default: all). */
    protected boolean include(CatalogProduct product) { return true; }

    /** Hook: optional closing line (default: none). */
    protected String footer(List<CatalogProduct> selected) { return ""; }
}
