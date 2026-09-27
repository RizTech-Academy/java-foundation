package com.riztech.tiffin;

import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

public final class Main {

    public static void main(String[] args) {
        try {
            System.exit(run(args));
        } catch (IllegalArgumentException e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(2);
        } catch (UncheckedIOException e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(3);
        }
    }

    static int run(String[] args) {
        if (args.length == 0 || args[0].equals("help")) {
            usage();
            return args.length == 0 ? 1 : 0;
        }

        Path dataFile = Path.of(System.getProperty("tiffin.data", "deliveries.csv"));
        DeliveryStore store = new DeliveryStore(dataFile);

        return switch (args[0]) {
            case "add" -> add(store, args);
            case "list" -> list(store);
            case "report" -> report(store, args);
            default -> {
                System.err.println("Unknown command [" + args[0] + "]");
                usage();
                yield 1;
            }
        };
    }

    private static int add(DeliveryStore store, String[] args) {
        if (args.length != 4) {
            System.err.println("Usage: add <yyyy-mm-dd> <customer> <tiffins>");
            return 1;
        }
        Parsed<Delivery> parsed = store.load();
        List<Delivery> all = new ArrayList<>(parsed.rows());
        all.add(new Delivery(LocalDate.parse(args[1]), args[2], Integer.parseInt(args[3])));
        store.save(all);
        System.out.println("Added. " + all.size() + " deliveries on file.");
        return 0;
    }

    private static int list(DeliveryStore store) {
        Parsed<Delivery> parsed = store.load();
        parsed.rows().forEach(d ->
                System.out.printf("%s  %-20s %d%n", d.date(), d.customer(), d.tiffins()));
        reportProblems(parsed);
        return parsed.hasProblems() ? 4 : 0;
    }

    private static int report(DeliveryStore store, String[] args) {
        YearMonth month = args.length > 1 ? YearMonth.parse(args[1]) : YearMonth.now();
        Parsed<Delivery> parsed = store.load();
        reportProblems(parsed);

        BillingService billing = new BillingService(List.of(
                new Subscriber("Priya", "411207", Plan.VEG, LocalDate.of(2026, 9, 1)),
                new Subscriber("Arjun", "411014", Plan.JAIN, LocalDate.of(2026, 9, 1)),
                new Subscriber("Kavita", "411207", Plan.STUDENT, LocalDate.of(2026, 9, 1))));

        System.out.print(billing.renderReport(parsed.rows(), month));
        billing.busiestDay(parsed.rows())
                .ifPresent(d -> System.out.println("Busiest day: " + d));
        return parsed.hasProblems() ? 4 : 0;
    }

    private static void reportProblems(Parsed<Delivery> parsed) {
        if (parsed.hasProblems()) {
            System.err.println(parsed.problems().size() + " row(s) could not be read:");
            parsed.problems().forEach(p -> System.err.println("  " + p));
        }
    }

    private static void usage() {
        System.out.println("""
                tiffin-tracker

                  add <yyyy-mm-dd> <customer> <tiffins>   record a delivery
                  list                                    show every delivery
                  report [yyyy-mm]                        bill for a month
                  help                                    this message

                Data file: -Dtiffin.data=<path> (default deliveries.csv)""");
    }
}
