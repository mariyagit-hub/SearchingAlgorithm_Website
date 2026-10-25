package edu.naap.searching.ui;

import java.util.Arrays;

public final class InputValidator {
    private InputValidator() {
    }

    public static int[] parseArray(String text) {
        String input = text.trim();
        if (input.isEmpty()) {
            return new int[0];
        }
        String[] tokens = input.split(",", -1);
        if (tokens.length > 100) {
            throw new IllegalArgumentException("Enter no more than 100 integers so the visualization stays readable.");
        }
        int[] values = new int[tokens.length];
        for (int i = 0; i < tokens.length; i++) {
            String token = tokens[i].trim();
            if (token.isEmpty()) {
                throw new IllegalArgumentException("Array values must be integers separated by commas. Check for an empty value.");
            }
            try {
                values[i] = Integer.parseInt(token);
            } catch (NumberFormatException exception) {
                throw new IllegalArgumentException("“" + token + "” is not a valid 32-bit integer.");
            }
        }
        return values;
    }

    public static int parseTarget(String text) {
        String input = text.trim();
        if (input.isEmpty()) {
            throw new IllegalArgumentException("Enter an integer target value.");
        }
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Target must be a valid 32-bit integer.");
        }
    }

    public static boolean isSorted(int[] values) {
        return Arrays.equals(values, Arrays.stream(values).sorted().toArray());
    }
}
