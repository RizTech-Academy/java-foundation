package com.riztech.tiffin;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DeliveryStoreTest {

    @TempDir
    Path dir;

    @Test
    void loadsGoodRowsAndReportsBadOnesWithLineNumbers() throws IOException {
        Path file = dir.resolve("deliveries.csv");
        Files.writeString(file, """
                date,customer,tiffins
                2026-09-01,Priya,2
                2026-09-01,Arjun
                2026-09-02,Kavita,notanumber
                2026-09-02,Amit,99
                2026-09-03,Priya,1
                """);

        Parsed<Delivery> parsed = new DeliveryStore(file).load();

        assertEquals(2, parsed.rows().size());
        assertEquals(3, parsed.problems().size());
        assertTrue(parsed.problems().get(0).startsWith("line 3:"), parsed.problems().get(0));
        assertTrue(parsed.problems().get(1).contains("notanumber"), parsed.problems().get(1));
        assertTrue(parsed.problems().get(2).contains("between 0 and 4"), parsed.problems().get(2));
    }

    @Test
    void missingFileIsEmptyRatherThanAnError() {
        Parsed<Delivery> parsed = new DeliveryStore(dir.resolve("nope.csv")).load();
        assertTrue(parsed.rows().isEmpty());
        assertFalse(parsed.hasProblems());
    }

    @Test
    void roundTripsThroughSaveAndLoad() {
        Path file = dir.resolve("out.csv");
        List<Delivery> original = List.of(
                new Delivery(LocalDate.of(2026, 9, 1), "Priya", 2),
                new Delivery(LocalDate.of(2026, 9, 2), "Kale, Kavita", 3));

        DeliveryStore store = new DeliveryStore(file);
        store.save(original);

        assertEquals(original, store.load().rows());
    }

    @Test
    void savingLeavesNoTemporaryFileBehind() throws IOException {
        Path file = dir.resolve("out.csv");
        new DeliveryStore(file).save(List.of(new Delivery(LocalDate.of(2026, 9, 1), "Priya", 2)));

        try (DirectoryStream<Path> entries = Files.newDirectoryStream(dir)) {
            for (Path p : entries) {
                assertFalse(p.getFileName().toString().endsWith(".tmp"),
                        "temporary file left behind: " + p);
            }
        }
    }

    @Test
    void stripsAByteOrderMarkFromTheHeader() throws IOException {
        Path file = dir.resolve("bom.csv");
        Files.writeString(file, "﻿date,customer,tiffins\n2026-09-01,Priya,2\n");

        Parsed<Delivery> parsed = new DeliveryStore(file).load();

        assertEquals(1, parsed.rows().size());
        assertFalse(parsed.hasProblems(), "BOM should not produce a problem row");
    }
}
