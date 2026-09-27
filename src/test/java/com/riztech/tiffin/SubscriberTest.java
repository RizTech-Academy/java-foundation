package com.riztech.tiffin;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class SubscriberTest {

    private static final LocalDate START = LocalDate.of(2026, 9, 1);

    @Test
    void stripsWhitespaceFromTheName() {
        assertEquals("Priya", new Subscriber("  Priya  ", "411207", Plan.VEG, START).name());
    }

    @Test
    void rejectsABlankName() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> new Subscriber("   ", "411207", Plan.VEG, START));
        assertEquals("name must not be blank", e.getMessage());
    }

    @Test
    void rejectsAShortPincodeAndQuotesIt() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> new Subscriber("Priya", "41120", Plan.VEG, START));
        assertEquals("pincode must be six digits, got [41120]", e.getMessage());
    }

    @Test
    void rejectsNullsByName() {
        NullPointerException e = assertThrows(NullPointerException.class,
                () -> new Subscriber(null, "411207", Plan.VEG, START));
        assertEquals("name must not be null", e.getMessage());
    }

    @Test
    void twoSubscribersWithTheSameValuesAreEqual() {
        assertEquals(new Subscriber("Priya", "411207", Plan.VEG, START),
                new Subscriber("Priya", "411207", Plan.VEG, START));
    }

    @Test
    void planParsingIsCaseInsensitiveAndTrims() {
        assertEquals(Plan.VEG, Plan.parse(" veg "));
        assertEquals(Plan.JAIN, Plan.parse("Jain"));
    }

    @Test
    void unknownPlanListsTheValidOnes() {
        IllegalArgumentException e =
                assertThrows(IllegalArgumentException.class, () -> Plan.parse("keto"));
        assertTrue(e.getMessage().contains("keto"), e.getMessage());
        assertTrue(e.getMessage().contains("VEG"), e.getMessage());
    }
}
