package com.bazaarehunar.patterns.structural.composite;

import java.util.List;

public record CategoryLeaf(String name) implements CategoryComponent {
    @Override
    public List<CategoryComponent> children() {
        return List.of();
    }
}
