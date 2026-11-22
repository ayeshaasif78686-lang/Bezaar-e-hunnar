package com.bazaarehunar.patterns.behavioral.chainofresponsibility;

public record ValidationResult(boolean valid, String failedAt, String reason) {
    public static ValidationResult ok() { return new ValidationResult(true, null, null); }
    public static ValidationResult fail(String handler, String reason) { return new ValidationResult(false, handler, reason); }
}
