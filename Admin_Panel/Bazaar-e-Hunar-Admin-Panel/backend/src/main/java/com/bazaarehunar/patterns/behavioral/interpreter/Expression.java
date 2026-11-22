package com.bazaarehunar.patterns.behavioral.interpreter;

import com.bazaarehunar.model.CatalogProduct;

/**
 * INTERPRETER (behavioral).
 * Abstract expression of a tiny product-search language, e.g.
 * <pre>category:pottery AND (price&lt;5000 OR rating&gt;=4.5) AND NOT region:Sindh</pre>
 * Each grammar rule is a class; a query is parsed into a tree of expressions that is then interpreted
 * against a product.
 */
public interface Expression {
    boolean interpret(CatalogProduct product);
}
