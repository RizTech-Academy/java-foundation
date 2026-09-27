package com.riztech.tiffin;

import java.util.List;

/** Every good row, and every bad one with the line number and the reason. */
public record Parsed<T>(List<T> rows, List<String> problems) {

    public Parsed {
        rows = List.copyOf(rows);
        problems = List.copyOf(problems);
    }

    public boolean hasProblems() {
        return !problems.isEmpty();
    }
}
