package ru.chipmunkbarcode.util.gs1;

public class CheckDigit {

    /**
     * Calculates the check digit on a sequence of digits.
     *
     * @throws NullPointerException     if the input string is null
     * @throws IllegalArgumentException if the input string is not a sequence of at least one digit
     */
    public static char calculate(String s) {
        if (s == null) {
            throw new NullPointerException();
        }
        if (!Internals.isDigits(s)) {
            throw new IllegalArgumentException("Invalid sequence, must be digits");
        }
        return checksum(s);
    }

    /**
     * Calculates the check digit on a sequence of digits and returns it appended to the end of the sequence.
     *
     * @throws NullPointerException     if the input string is null
     * @throws IllegalArgumentException if the input string is not a sequence of at least one digit
     */
    public static String calculateAndAppend(String s) {
        char checkDigit = calculate(s);
        return s + checkDigit;
    }

    /**
     * Calculates the check digit on a sequence of digits ignoring the last digit which is assumed to already be a check
     * digit.
     *
     * @throws NullPointerException     if the input string is null
     * @throws IllegalArgumentException if the input string is not a sequence of at least two digits
     */
    public static char recalculate(String s) {
        if (s == null) {
            throw new NullPointerException();
        }
        if (!Internals.isDigits(s)) {
            throw new IllegalArgumentException("Invalid sequence, must be digits");
        }
        if (s.length() < 2) {
            throw new IllegalArgumentException("Invalid sequence, must be at least 2 digits");
        }
        return checksum(s.substring(0, s.length() - 1));
    }

    /**
     * Calculates the check digit on a sequence of digits ignoring the last digit which is assumed to already be a check
     * digit and returns the sequence with the check digit replaced.
     *
     * @throws NullPointerException     if the input string is null
     * @throws IllegalArgumentException if the input string is not a sequence of at least two digits
     */
    public static String recalculateAndApply(String s) {
        char checkDigit = recalculate(s);
        return s.substring(0, s.length() - 1) + checkDigit;
    }

    /**
     * Determines if the check digit in a digit sequence is correct. Returns null if the input string is null or not a
     * sequence of at least two digits.
     */
    public static boolean isValid(String s) {
        if (s == null || !Internals.isDigits(s) || s.length() < 2) {
            return false;
        }
        char calculated = checksum(s.substring(0, s.length() - 1));
        char actual = s.charAt(s.length() - 1);
        return actual == calculated;
    }

    /**
     * Checks if the check digit in a digit sequence is correct.
     *
     * @throws IllegalArgumentException if the input string is null, not a sequence of at least two digits or if the check digit is not correct
     */
    public static String validate(String s) {
        if (!isValid(s)) {
            throw new IllegalArgumentException("Check digit is not correct");
        }
        return s;
    }

    /**
     * Calculates a check digit for a sequence of digits where digits in odd positions have weight 3 and even positions
     * have a weight of 1.
     */
    private static char checksum(String s) {
        int sum = 0;
        for (int i = 0, position = s.length(); i < s.length(); i++, position--) {
            int n = s.charAt(i) - '0';
            sum += n + (n + n) * (position & 1);
        }
        return (char) ('0' + ((10 - (sum % 10)) % 10));
    }
}
