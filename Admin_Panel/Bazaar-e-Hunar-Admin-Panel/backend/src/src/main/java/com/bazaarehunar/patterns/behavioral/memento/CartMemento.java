package com.bazaarehunar.patterns.behavioral.memento;

import java.util.Map;

public record CartMemento(Map<String, Integer> quantities) { }
