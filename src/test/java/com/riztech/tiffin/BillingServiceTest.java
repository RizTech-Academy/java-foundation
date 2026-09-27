package com.riztech.tiffin;

import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class BillingServiceTest {

    private BillingService billing;

    @BeforeEach
    void setUp() {
        billing = new BillingService(List.of(
                new Subscriber("Priya", "411207", Plan.VEG, LocalDate.of(2026, 9, 1)),
                new Subscriber("Arjun", "411014", Plan.JAIN, LocalDate.of(2026, 9, 1))));
    }

    private static final List<Delivery> DELIVERIES = List.of(
            new Delivery(LocalDate.of(2026, 9, 1), "Priya", 2),
            new Delivery(LocalDate.of(2026, 9, 1), "Arjun", 1),
            new Delivery(LocalDate.of(2026, 9, 2), "Priya", 1),
            new Delivery(LocalDate.of(2026, 8, 31), "Priya", 4));

    @Test
    void countsOnlyTheRequestedMonth() {
        Map<String, Integer> counts = billing.tiffinsPerCustomer(DELIVERIES, YearMonth.of(2026, 9));
        assertEquals(Map.of("Priya", 3, "Arjun", 1), counts);
    }

    @Test
    void billsAtThePlanPrice() {
        assertEquals(3 * 8235, billing.billPaise("Priya", 3));
        assertEquals(1 * 9100, billing.billPaise("Arjun", 1));
    }

    @Test
    void namesTheCustomerWhenThereIsNoSuchSubscriber() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> billing.billPaise("Nobody", 1));
        assertEquals("no subscriber named [Nobody]", e.getMessage());
    }

    @Test
    void picksTheBusiestDayByTiffinsNotByRowCount() {
        assertEquals(LocalDate.of(2026, 8, 31), billing.busiestDay(DELIVERIES).orElseThrow());
    }

    @Test
    void hasNoBusiestDayWhenThereAreNoDeliveries() {
        assertTrue(billing.busiestDay(List.of()).isEmpty());
    }

    @Test
    void reportTotalsMatchTheIndividualBills() {
        Map<String, Long> bills = billing.billsFor(DELIVERIES, YearMonth.of(2026, 9));
        long expected = bills.values().stream().mapToLong(Long::longValue).sum();
        String report = billing.renderReport(DELIVERIES, YearMonth.of(2026, 9));
        assertTrue(report.contains(Money.format(expected)),
                "report should contain the total " + Money.format(expected) + "\n" + report);
    }

    @Nested
    @DisplayName("an empty month")
    class EmptyMonth {

        @Test
        void billsNothing() {
            assertTrue(billing.billsFor(DELIVERIES, YearMonth.of(2026, 1)).isEmpty());
        }

        @Test
        void stillRendersAReportWithAZeroTotal() {
            String report = billing.renderReport(DELIVERIES, YearMonth.of(2026, 1));
            assertTrue(report.contains("Rs 0.00"), report);
        }
    }
}
