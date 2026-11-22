package com.bazaarehunar.patterns.structural.adapter;

/** Adaptee 1: a third-party style client with its own incompatible method names and types. */
public class LegacyEasyPaisaClient {
    /** @return a transaction id, or null when the wallet transfer is refused. */
    public String sendMoney(String msisdn, double rupees, String memo) {
        if (msisdn == null || msisdn.length() < 10 || rupees <= 0) return null;
        return "EP-" + msisdn.substring(msisdn.length() - 4) + "-" + Math.round(rupees * 100);
    }
}
