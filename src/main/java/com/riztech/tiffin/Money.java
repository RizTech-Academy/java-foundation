package com.riztech.tiffin;

/** Money is always paise. Never a double. */
public final class Money {

    private Money() {
    }

    public static String format(long paise) {
        String sign = paise < 0 ? "-" : "";
        long abs = Math.abs(paise);
        return "%sRs %,d.%02d".formatted(sign, abs / 100, abs % 100);
    }

    public static long parseRupees(String text) {
        String trimmed = text.strip().replace(",", "");
        if (!trimmed.matches("-?\\d+(\\.\\d{1,2})?")) {
            throw new IllegalArgumentException("not an amount in rupees: [" + text + "]");
        }
        boolean negative = trimmed.startsWith("-");
        String digits = negative ? trimmed.substring(1) : trimmed;
        int dot = digits.indexOf('.');
        long rupees = Long.parseLong(dot < 0 ? digits : digits.substring(0, dot));
        long paise = 0;
        if (dot >= 0) {
            String fraction = (digits.substring(dot + 1) + "00").substring(0, 2);
            paise = Long.parseLong(fraction);
        }
        long total = rupees * 100 + paise;
        return negative ? -total : total;
    }
}
