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
        // Six digits and never a leading zero: no Indian pincode starts with 0.
        // "\\d{6}" looks right and quietly accepts 012345.
        if (!pincode.matches("[1-9]\\d{5}")) {
            throw new IllegalArgumentException(
                    "pincode must be six digits not starting with zero, got [" + pincode + "]");
        }
    }

    public long pricePerTiffinPaise() {
        return plan.pricePaise();
    }
}
