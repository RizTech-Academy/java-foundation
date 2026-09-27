package com.riztech.tiffin;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class MoneyTest {

    @Test
    @DisplayName("formats whole rupees with a thousands separator")
    void formatsLargeAmounts() {
        assertEquals("Rs 2,141.10", Money.format(214110));
    }

    @Test
    @DisplayName("pads paise below ten so 5 paise is not 50")
    void padsPaise() {
        assertEquals("Rs 0.05", Money.format(5));
    }

    @Test
    @DisplayName("puts the sign before the currency, not between the halves")
    void formatsNegatives() {
        assertEquals("-Rs 12.50", Money.format(-1250));
    }

    @ParameterizedTest
    @CsvSource({
            "82.35,  8235",
            "82.3,   8230",
            "82,     8200",
            "0.05,      5",
            "1234.56, 123456",
            "-12.50, -1250"
    })
    void parsesRupees(String text, long expectedPaise) {
        assertEquals(expectedPaise, Money.parseRupees(text));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "abc", "82.345", "12,34,567.8.9", "Rs 82"})
    void rejectsRubbish(String text) {
        IllegalArgumentException e =
                assertThrows(IllegalArgumentException.class, () -> Money.parseRupees(text));
        assertTrue(e.getMessage().contains(text.strip()),
                "message should quote the offending input, was: " + e.getMessage());
    }

    @Test
    @DisplayName("round trips through format and parse")
    void roundTrips() {
        for (long paise : new long[]{0, 5, 100, 8235, 214110, 123456789}) {
            String formatted = Money.format(paise).replace("Rs ", "");
            assertEquals(paise, Money.parseRupees(formatted), "round trip failed for " + paise);
        }
    }
}
