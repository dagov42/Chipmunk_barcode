package ru.chipmunkbarcode.util.gs1;

public class GLN {

    public static final int LENGTH = 13;

    /**
     * Determines if a string is a valid GLN without verifying its check digit.
     */
    public static boolean isGLN(String gln) {
        return Internals.isDigits(gln) && gln.length() == LENGTH;
    }

    /**
     * Validates that a string is a GLN with a correct check digit.
     */
    public static boolean isValid(String gln) {
        return isGLN(gln) && CheckDigit.isValid(gln);
    }

    /**
     * Checks if a string is a correctly formatted GLN without verifying its check digit.
     *
     * @throws NullPointerException     if the input string is null
     * @throws IllegalArgumentException if the input string is not a sequence of exactly 13 digits
     */
    public static String validateFormat(String gln) {
        return Internals.validateFormat("GLN", LENGTH, gln);
    }

    /**
     * Checks if a string is a GLN with correct check digit.
     *
     * @throws NullPointerException     if the input string is null
     * @throws IllegalArgumentException if the input string is not a sequence of exactly 13 digits or if the check digit is not correct
     */
    public static String validateFormatAndCheckDigit(String gln) {
        return CheckDigit.validate(validateFormat(gln));
    }
}
