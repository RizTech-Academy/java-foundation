package com.riztech.tiffin;

import java.time.LocalDate;
import java.util.Objects;

public record Subscriber(String name, String pincode, Plan plan, LocalDate startedOn) {

    public Subscriber {
        Objects.requireNonNull(name, "name must not be null");
        Objects.requireNonNull(pincode, "pincode must not be null");
        Objects.requireNonNull(plan, "plan must not be null");
        Objects.requireNonNull(startedOn, "startedOn must not be null");

        name = name.strip();
        if (name.isBlank()) {
            throw new IllegalArgumentException("name must not be blank");
        }
        if (!pincode.matches("\\d{6}")) {
            throw new IllegalArgumentException("pincode must be six digits, got [" + pincode + "]");
        }
    }

    public long pricePerTiffinPaise() {
        return plan.pricePaise();
    }
}
