package com.riztech.tiffin;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Reads and writes the delivery CSV. Writes are atomic. */
public final class DeliveryStore {

    private static final String HEADER = "date,customer,tiffins";
    private final Path file;

    public DeliveryStore(Path file) {
        this.file = file;
    }

    public Parsed<Delivery> load() {
        if (Files.notExists(file)) {
            return new Parsed<>(List.of(), List.of());
        }
        List<String> lines;
        try {
            lines = Files.readAllLines(file, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException("could not read " + file, e);
        }

        List<Delivery> rows = new ArrayList<>();
        List<String> problems = new ArrayList<>();
        for (int i = 0; i < lines.size(); i++) {
            int lineNumber = i + 1;
            String line = stripBom(lines.get(i));
            if (line.isBlank() || (lineNumber == 1 && line.equalsIgnoreCase(HEADER))) {
                continue;
            }
            String[] parts = splitCsv(line);
            if (parts.length != 3) {
                problems.add("line %d: expected 3 fields, found %d".formatted(lineNumber, parts.length));
                continue;
            }
            try {
                rows.add(new Delivery(
                        LocalDate.parse(parts[0].strip()),
                        parts[1],
                        Integer.parseInt(parts[2].strip())));
            } catch (RuntimeException e) {
                problems.add("line %d: %s".formatted(lineNumber, e.getMessage()));
            }
        }
        return new Parsed<>(rows, problems);
    }

    public void save(List<Delivery> deliveries) {
        StringBuilder sb = new StringBuilder(HEADER).append('\n');
        for (Delivery d : deliveries) {
            sb.append(d.date()).append(',')
              .append(quote(d.customer())).append(',')
              .append(d.tiffins()).append('\n');
        }
        writeAtomically(sb.toString());
    }

    private void writeAtomically(String content) {
        Path tmp = file.resolveSibling(file.getFileName() + ".tmp");
        try {
            if (file.getParent() != null) {
                Files.createDirectories(file.getParent());
            }
            Files.writeString(tmp, content, StandardCharsets.UTF_8);
            Files.move(tmp, file, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException e) {
            throw new UncheckedIOException("could not write " + file, e);
        }
    }

    static String quote(String field) {
        if (field.contains(",") || field.contains("\"") || field.contains("\n")) {
            return '"' + field.replace("\"", "\"\"") + '"';
        }
        return field;
    }

    static String stripBom(String line) {
        return line.isEmpty() || line.charAt(0) != '﻿' ? line : line.substring(1);
    }

    /**
     * Quote-aware field splitter. A plain split(",") loses a row whose customer
     * name contains a comma — which the writer quotes correctly, so the reader
     * has to understand quoting too.
     */
    static String[] splitCsv(String line) {
        List<String> out = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (inQuotes) {
                if (c == '"') {
                    if (i + 1 < line.length() && line.charAt(i + 1) == '"') {
                        current.append('"');
                        i++;
                    } else {
                        inQuotes = false;
                    }
                } else {
                    current.append(c);
                }
            } else if (c == '"') {
                inQuotes = true;
            } else if (c == ',') {
                out.add(current.toString());
                current.setLength(0);
            } else {
                current.append(c);
            }
        }
        out.add(current.toString());
        return out.toArray(new String[0]);
    }
}
