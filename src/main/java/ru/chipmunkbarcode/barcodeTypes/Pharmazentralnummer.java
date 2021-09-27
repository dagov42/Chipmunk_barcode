package ru.chipmunkbarcode.barcodeTypes;

import ru.chipmunkbarcode.exceptions.BarcodeException;

/**
 * PZN8 is a Code 39 based symbology used by the pharmaceutical industry in
 * Germany. PZN8 encodes a 7 digit number and includes a modulo-10 check digit.
 */
public class Pharmazentralnummer extends Symbol {

    /* Pharmazentral Nummer is a Code 3 of 9 symbol with an extra
     * check digit. Now generates PZN-8.
     */

    @Override
    protected void encode() {
        int l = content.length();
        String localstr;
        int zeroes, count = 0, check_digit;
        Code3Of9 c = new Code3Of9();

        if (l > 7) {
            throw new BarcodeException("Input data too long");
        }

        if (!content.matches("[0-9]+")) {
            throw new BarcodeException("Invalid characters in input");
        }

        localstr = "-";
        zeroes = 7 - l + 1;
        for (int i = 1; i < zeroes; i++)
            localstr += '0';

        localstr += content;

        for (int i = 1; i < 8; i++) {
            count += i * Character.getNumericValue(localstr.charAt(i));
        }

        check_digit = count % 11;
        if (check_digit == 11) {
            check_digit = 0;
        }
        if (check_digit == 10) {
            throw new BarcodeException("Not a valid PZN identifier");
        }

        infoLine("Check Digit: " + check_digit);

        localstr += (char) (check_digit + '0');

        c.setContent(localstr);

        readable = "PZN" + localstr;
        pattern = new String[1];
        pattern[0] = c.pattern[0];
        row_count = 1;
        rowHeight = new int[1];
        rowHeight[0] = -1;
    }
}
