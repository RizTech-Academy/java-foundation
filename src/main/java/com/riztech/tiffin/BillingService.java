package com.riztech.tiffin;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

public final class BillingService {

    private final Map<String, Subscriber> subscribers;

    public BillingService(List<Subscriber> subscribers) {
        this.subscribers = subscribers.stream()
                .collect(Collectors.toMap(Subscriber::name, s -> s, (a, b) -> a, LinkedHashMap::new));
    }

    public Map<String, Integer> tiffinsPerCustomer(List<Delivery> deliveries, YearMonth month) {
        return deliveries.stream()
                .filter(d -> YearMonth.from(d.date()).equals(month))
                .collect(Collectors.groupingBy(Delivery::customer, TreeMap::new,
                        Collectors.summingInt(Delivery::tiffins)));
    }

    public long billPaise(String customer, int tiffins) {
        Subscriber s = subscribers.get(customer);
        if (s == null) {
            throw new IllegalArgumentException("no subscriber named [" + customer + "]");
        }
        return (long) tiffins * s.pricePerTiffinPaise();
    }

    public Map<String, Long> billsFor(List<Delivery> deliveries, YearMonth month) {
        Map<String, Long> bills = new TreeMap<>();
        tiffinsPerCustomer(deliveries, month)
                .forEach((customer, tiffins) -> bills.put(customer, billPaise(customer, tiffins)));
        return bills;
    }

    public Optional<LocalDate> busiestDay(List<Delivery> deliveries) {
        return deliveries.stream()
                .collect(Collectors.groupingBy(Delivery::date, Collectors.summingInt(Delivery::tiffins)))
                .entrySet().stream()
                .max(Map.Entry.<LocalDate, Integer>comparingByValue()
                        .thenComparing(Map.Entry.comparingByKey(Comparator.reverseOrder())))
                .map(Map.Entry::getKey);
    }

    /** Every name in the deliveries that is not a registered subscriber. */
    public List<String> unknownCustomers(List<Delivery> deliveries, YearMonth month) {
        return tiffinsPerCustomer(deliveries, month).keySet().stream()
                .filter(name -> !subscribers.containsKey(name))
                .toList();
    }

    public String renderReport(List<Delivery> deliveries, YearMonth month) {
        Map<String, Integer> counts = tiffinsPerCustomer(deliveries, month);
        StringBuilder sb = new StringBuilder();
        String title = "Tiffin bill - " + month;
        sb.append(title).append('\n').append("=".repeat(title.length())).append('\n');
        sb.append("%-20s%10s%14s%n".formatted("Customer", "Tiffins", "Amount"));

        long total = 0;
        List<String> unknown = new ArrayList<>();

        for (Map.Entry<String, Integer> e : counts.entrySet()) {
            // A delivery can be recorded for somebody who was never registered
            // — a guest, or a typo in the name. Throwing here would abandon the
            // whole month's bill over one row, so the row is set aside and
            // listed at the end instead. A report that refuses to print is no
            // use to a shopkeeper at the end of the month.
            if (!subscribers.containsKey(e.getKey())) {
                unknown.add(e.getKey());
                continue;
            }
            long paise = billPaise(e.getKey(), e.getValue());
            total += paise;
            sb.append("%-20s%10d%14s%n".formatted(e.getKey(), e.getValue(), Money.format(paise)));
        }

        sb.append("%-20s%10s%14s%n".formatted("Total", "", Money.format(total)));

        if (!unknown.isEmpty()) {
            sb.append('\n').append("Not billed - no subscriber registered:").append('\n');
            for (String name : unknown) {
                sb.append("  %-20s%10d%n".formatted(name, counts.get(name)));
            }
        }

        return sb.toString();
    }
}
