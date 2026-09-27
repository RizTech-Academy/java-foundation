package com.riztech.tiffin;

import java.util.Arrays;

public enum Plan {
    VEG("Vegetarian", 8_235),
    JAIN("Jain (no onion)", 9_100),
    STUDENT("Student", 7_412),
    TRIAL("Trial", 0);

    private final String label;
    private final int pricePaise;

    Plan(String label, int pricePaise) {
        this.label = label;
        this.pricePaise = pricePaise;
    }

    public String label() {
        return label;
    }

    public int pricePaise() {
        return pricePaise;
    }

    public static Plan parse(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new IllegalArgumentException("plan must not be blank");
        }
        try {
            return Plan.valueOf(raw.strip().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "unknown plan [" + raw + "], expected one of " + Arrays.toString(values()));
        }
    }
}
