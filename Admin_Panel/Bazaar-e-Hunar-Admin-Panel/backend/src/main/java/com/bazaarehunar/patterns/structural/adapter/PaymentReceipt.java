package com.bazaarehunar.patterns.structural.adapter;

public record PaymentReceipt(String gateway, String reference, int amountPkr, boolean success, String message) { }
