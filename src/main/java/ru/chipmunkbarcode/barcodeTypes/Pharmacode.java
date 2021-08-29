package ru.chipmunkbarcode.barcodeTypes;

import ru.chipmunkbarcode.exceptions.BarcodeException;

/**
 * Implements the <a href="http://en.wikipedia.org/wiki/Pharmacode">Pharmacode</a>
 * bar code symbology.
 * <br>
 * Pharmacode is used for the identification of pharmaceuticals. The symbology
 * is able to encode whole numbers between 3 and 131070.
 */
public class Pharmacode extends Symbol {

    @Override
    protected void encode() {
        int tester = 0;
        int i;

        String inter = "";
        String dest = "";

        if (content.length() > 6) {
            throw new BarcodeException("Input too long");
        }

        if (!content.matches("[0-9]+")) {
            throw new BarcodeException("Invalid characters in data");
        }

        for (i = 0; i < content.length(); i++) {
            tester *= 10;
            tester += Character.getNumericValue(content.charAt(i));
        }

        if (tester < 3 || tester > 131070) {
            throw new BarcodeException("Data out of range");
        }

        do {
            if ((tester & 1) == 0) {
                inter += "W";
                tester = (tester - 2) / 2;
            } else {
                inter += "N";
                tester = (tester - 1) / 2;
            }
        } while (tester != 0);

        for (i = inter.length() - 1; i >= 0; i--) {
            if (inter.charAt(i) == 'W') {
                dest += "32";
            } else {
                dest += "12";
            }
        }

        readable = "";
        pattern = new String[1];
        pattern[0] = dest;
        row_count = 1;
        row_height = new int[1];
        row_height[0] = -1;
    }
}
