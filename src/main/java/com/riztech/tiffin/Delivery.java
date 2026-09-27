package com.riztech.tiffin;

import java.time.LocalDate;
import java.util.Objects;

public record Delivery(LocalDate date, String customer, int tiffins) {

    public static final int MAX_PER_DAY = 4;

    public Delivery {
        Objects.requireNonNull(date, "date must not be null");
        Objects.requireNonNull(customer, "customer must not be null");
        customer = customer.strip();
        if (customer.isBlank()) {
            throw new IllegalArgumentException("customer must not be blank");
        }
        if (tiffins < 0 || tiffins > MAX_PER_DAY) {
            throw new IllegalArgumentException(
                    "tiffins must be between 0 and " + MAX_PER_DAY + ", got " + tiffins);
        }
    }
}
