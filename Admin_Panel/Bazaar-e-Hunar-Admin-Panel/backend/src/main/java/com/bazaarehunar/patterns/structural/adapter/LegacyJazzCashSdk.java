package com.bazaarehunar.patterns.structural.adapter;

/** Adaptee 2: works in paisa and reports success with status codes instead of objects. */
public class LegacyJazzCashSdk {
    private String lastReference;

    /** @return 0 on success, non-zero error code otherwise. */
    public int pay(String mobile, long paisa) {
        if (mobile == null || mobile.length() < 10) return 101;
        if (paisa <= 0) return 102;
        lastReference = "JC-" + mobile.substring(mobile.length() - 4) + "-" + paisa;
        return 0;
    }

    public String lastReference() { return lastReference; }
}
