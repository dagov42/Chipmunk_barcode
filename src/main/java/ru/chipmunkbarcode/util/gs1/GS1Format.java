package ru.chipmunkbarcode.util.gs1;

import ru.chipmunkbarcode.exceptions.BarcodeException;

/**
 * GS1 utility class.
 */
public class GS1Format {

    /**
     * Verifies that the specified data is in good GS1 format {@code "[AI]data"} pairs, and returns a reduced
     * version of the input string containing FNC1 escape sequences instead of AI brackets. With a few small
     * exceptions, this code matches the Zint GS1 validation code as closely as possible, in order to make it
     * easier to keep in sync.
     *
     * @param s    the data string to verify
     * @return the input data, verified and with FNC1 strings added at the appropriate positions
     * @see <a href="http://www.gs1.org/docs/gsmp/barcodes/GS1_General_Specifications.pdf">GS1 specification</a>
     */
    public static String verify(String s) {

        // Enforce compliance with GS1 General Specification
        // http://www.gs1.org/docs/gsmp/barcodes/GS1_General_Specifications.pdf

        char[] source = s.toCharArray();
        /* Detect extended ASCII characters */
        for (char c : source) {
            if (c >= 128) {
                throw new BarcodeException("Extended ASCII characters are not supported by GS1");
            }
        }
        StringBuilder builder = new StringBuilder();
        ElementString.parse(s).getElementsByString().forEach((key, value) -> builder.append("(").append(key).append(")").append(value));
        return builder.toString();
    }
}
