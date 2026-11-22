package com.bazaarehunar.patterns.structural.composite;

import java.util.List;

public interface CategoryComponent {
    String name();
    List<CategoryComponent> children();
    default boolean leaf() { return children().isEmpty(); }
}
