package com.io.tedtalksapi.util;

import lombok.extern.slf4j.Slf4j;

import java.math.BigInteger;

@Slf4j
public class DataFormatterUtil {

    /**
     * Ensures that a view or like count is non-negative.
     * If the input is negative, logs a warning and returns zero.
     *
     * @param rawValue  The original BigInteger value
     * @param fieldName The field name for logging ("viewCount" or "likeCount")
     * @return Non-negative BigInteger
     */
    public static BigInteger sanitizeCount(String rawValue, String fieldName) {
        if (rawValue == null || rawValue.trim().isEmpty()) {
            log.warn("Null or empty value for {} detected. Setting to 0.", fieldName);
            return BigInteger.ZERO;
        }

        try {
            // Keep digits and a single leading '-'
            String cleaned = rawValue.trim().replaceAll("[^0-9]", "");

            BigInteger value = new BigInteger(cleaned);

            if (value.compareTo(BigInteger.ZERO) < 0) {
                log.warn("Negative value for {} detected: {}. Resetting to 0.",
                        fieldName, rawValue);
                System.out.println(Integer.toHexString(rawValue.charAt(0)));
                System.out.println(value);
                System.out.println(cleaned);

                return BigInteger.ZERO;
            }

            return value;

        } catch (NumberFormatException e) {
            log.warn("Invalid numeric value for {} detected: {}. Setting to 0.",
                    fieldName, rawValue);
            return BigInteger.ZERO;
        }
    }

    private static final java.util.NavigableMap<java.math.BigDecimal, String> SUFFIXES = new java.util.TreeMap<>();
    private static final java.util.NavigableMap<BigInteger, Double> STAR_THRESHOLDS = new java.util.TreeMap<>();

    static {
        STAR_THRESHOLDS.put(BigInteger.valueOf(5_000_000), 5.0);
        STAR_THRESHOLDS.put(BigInteger.valueOf(4_000_000), 4.5);
        STAR_THRESHOLDS.put(BigInteger.valueOf(3_000_000), 4.0);
        STAR_THRESHOLDS.put(BigInteger.valueOf(2_000_000), 3.5);
        STAR_THRESHOLDS.put(BigInteger.valueOf(1_000_000), 3.0);
        STAR_THRESHOLDS.put(BigInteger.valueOf(500_000), 2.5);
    }

    static {
        // Precomputed thresholds
        SUFFIXES.put(new java.math.BigDecimal("1E18"), "Qn"); // Quintillion
        SUFFIXES.put(new java.math.BigDecimal("1E15"), "Qd"); // Quadrillion
        SUFFIXES.put(new java.math.BigDecimal("1E12"), "T"); // Trillion
        SUFFIXES.put(new java.math.BigDecimal("1E9"), "B"); // Billion
        SUFFIXES.put(new java.math.BigDecimal("1E6"), "M"); // Million
        SUFFIXES.put(new java.math.BigDecimal("1E3"), "K"); // Thousand
    }

    /**
     * Returns a star rating from 1.0 to 5.0 based on BigInteger score.
     */
    public static double mapScoreToStars(BigInteger score) {
        if (score == null)
            return 1.0;
        var entry = STAR_THRESHOLDS.floorEntry(score);
        return (entry != null) ? entry.getValue() : 1.0;
    }

    public static String formatBigInteger(BigInteger value) {
        if (value == null)
            return "0";
        java.math.BigDecimal num = new java.math.BigDecimal(value);

        // Get the closest threshold <= number
        var entry = SUFFIXES.floorEntry(num);

        if (entry == null) {
            return num.toPlainString(); // number < 1000
        }

        java.math.BigDecimal divisor = entry.getKey();
        String suffix = entry.getValue();

        java.math.BigDecimal scaled = num.divide(divisor, 2, java.math.RoundingMode.HALF_UP);

        return normalizeAbbreviatedNumber(scaled.toPlainString() + suffix);
    }

    public static String normalizeAbbreviatedNumber(String input) {
        if (input == null || input.isBlank())
            return input;

        input = input.trim();

        // Detect suffix (K / M / B)
        String suffix = "";
        if (input.matches(".*[KMBkmb]$")) {
            suffix = input.substring(input.length() - 1).toUpperCase();
            input = input.substring(0, input.length() - 1);
        }

        // Clean number part (digits + decimal only)
        input = input.replaceAll("[^0-9.]", "");

        if (input.isEmpty())
            return suffix.isEmpty() ? "0" : "0" + suffix;

        // Remove leading zeros
        input = input.replaceFirst("^0+(?!$)", "");

        // Remove trailing zeros in decimals (205.00 -> 205, 4.70 -> 4.7)
        if (input.contains(".")) {
            input = input.replaceAll("0+$", "");
            input = input.replaceAll("\\.$", "");
        }

        return input + suffix;
    }
}
