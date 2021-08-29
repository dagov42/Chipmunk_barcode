package ru.chipmunkbarcode.util.gs1;

public class SSCC {

    public static final int LENGTH = 18;

    /**
     * Determines if a string is a valid SSCC without verifying its check digit.
     */
    public static boolean isSSCC(String sscc) {
        return Internals.isDigits(sscc) && sscc.length() == LENGTH;
    }

    /**
     * Validates that a string is a SSCC with a correct check digit.
     */
    public static boolean isValid(String sscc) {
        return isSSCC(sscc) && CheckDigit.isValid(sscc);
    }

    /**
     * Checks if a string is a correctly formatted SSCC without verifying its check digit.
     *
     * @throws NullPointerException     if the input string is null
     * @throws IllegalArgumentException if the input string is not a sequence of exactly 18 digits
     */
    public static String validateFormat(String gln) {
        return Internals.validateFormat("SSCC", LENGTH, gln);
    }

    /**
     * Checks if a string is a SSCC with correct check digit.
     *
     * @throws NullPointerException     if the input string is null
     * @throws IllegalArgumentException if the input string is not a sequence of exactly 18 digits or if the check digit is not correct
     */
    public static String validateFormatAndCheckDigit(String gln) {
        return CheckDigit.validate(validateFormat(gln));
    }

}
