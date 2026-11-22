package com.bazaarehunar.patterns.structural.decorator;

/** Custom embroidery/engraving priced per character of the personal message. */
public class EmbroideryDecorator extends PriceDecorator {
    private static final int BASE_FEE = 300;
    private static final int PER_CHARACTER = 20;
    private final String text;

    public EmbroideryDecorator(PricedItem inner, String text) {
        super(inner);
        this.text = text == null ? "" : text.trim();
    }

    @Override public int price() { return inner.price() + BASE_FEE + PER_CHARACTER * text.length(); }
    @Override public String description() { return inner.description() + " + embroidery \"" + text + "\""; }
}
