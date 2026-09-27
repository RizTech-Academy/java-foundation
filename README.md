# java-foundation

Reference code for the [RizTech Academy Java Complete Foundation
course](https://www.riztechacademy.com/learn/java-foundation) — a tiffin service
delivery tracker, built across module 11.

**Compare with this when you are stuck. Do not start from it.** The point of the
capstone is typing it yourself and fixing your own mistakes; a finished copy
only helps once you have something of your own to compare against.

## Running it

Java 21 or newer, and Maven.

```bash
mvn test
```

```bash
mvn package
```

```bash
java -jar target/tiffin-tracker.jar help
```

The jar is shaded, so it runs on its own with no classpath to assemble.

## Using it

```bash
java -jar target/tiffin-tracker.jar add 2026-09-01 Priya 2
java -jar target/tiffin-tracker.jar list
java -jar target/tiffin-tracker.jar report 2026-09
```

Deliveries are stored in `deliveries.csv` beside you. Point it somewhere else
with `-Dtiffin.data=<path>`.

## What is in it

```
Money.java            paise as a long, and the formatting
Plan.java             the subscription plans, as an enum
Subscriber.java       a record, validating in its compact constructor
Delivery.java         one delivery, parsed from a CSV row
Parsed.java           rows that read, and rows that did not — both kept
DeliveryStore.java    CSV that survives a comma inside a name
BillingService.java   grouping, totals and the monthly bill
Main.java             the command-line front
```

## Things worth reading the code for

**`Money` is a `long` of paise, never a `double`.** `0.1 + 0.2` is not `0.3` in
any language with floating point, and a billing system that is out by a paisa
per row is a billing system nobody trusts.

**`DeliveryStore` quotes on write and parses quotes on read.** A customer called
`Kale, Kavita` breaks any CSV reader written as `line.split(",")` — this one was
written that way first, and the round-trip test caught it.

**`Parsed<T>` keeps the rows that failed.** A single bad row in a data file must
not lose the other four hundred, so parsing returns both the rows and the
problems, and the command decides what to do about it.

**`renderReport` does not throw on an unregistered customer.** A delivery can be
recorded for somebody who was never registered — a guest, or a typo. Abandoning
the whole month's bill over one row would be no use to a shopkeeper at the end
of the month, so those rows are listed separately instead.

## Licence

MIT
