package com.bazaarehunar.patterns.behavioral.observer;

public record OrderEvent(String orderNumber, String oldStatus, String newStatus) { }
