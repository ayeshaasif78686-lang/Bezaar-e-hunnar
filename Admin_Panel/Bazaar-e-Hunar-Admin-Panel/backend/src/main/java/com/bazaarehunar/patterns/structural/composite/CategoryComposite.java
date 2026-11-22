package com.bazaarehunar.patterns.structural.composite;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CategoryComposite implements CategoryComponent {
    private final String name;
    private final List<CategoryComponent> children = new ArrayList<>();

    public CategoryComposite(String name) { this.name = name; }
    public CategoryComposite add(CategoryComponent component) { children.add(component); return this; }
    @Override public String name() { return name; }
    @Override public List<CategoryComponent> children() { return Collections.unmodifiableList(children); }
}
