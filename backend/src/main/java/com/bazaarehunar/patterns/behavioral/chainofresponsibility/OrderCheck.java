package com.bazaarehunar.patterns.behavioral.chainofresponsibility;

import java.util.Map;

/** The request object that travels along the validation chain. */
public record OrderCheck(Map<String, Integer> items, Map<String, Integer> stock, String fullName, String phone,
                         String address, String city, String province, String paymentMethod, int total) { }
