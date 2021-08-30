package ru.chipmunkbarcode.barcodeTypes;

import ru.chipmunkbarcode.exceptions.BarcodeException;

/**
 * <p>Implements Code 32, also known as Italian Pharmacode, A variation of Code
 * 39 used by the Italian Ministry of Health ("Ministero della Sanità")
 *
 * <p>Requires a numeric input up to 8 digits in length. Check digit is
 * calculated.
 */
public class Code32 extends Symbol {

    private static final char[] TABLE = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'B', 'C', 'D', 'F',
            'G', 'H', 'J', 'K', 'L', 'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V',
            'W', 'X', 'Y', 'Z'
    };

    @Override
    protected void encode() {
        int i, checksum, checkpart, checkdigit;
        int pharmacode, remainder, devisor;
        String localstr, risultante;
        int[] codeword = new int[6];
        Code3Of9 c39 = new Code3Of9();

        if (content.length() > 8) {
            throw new BarcodeException("Input too long");
        }

        if (!content.matches("[0-9]+")) {
            throw new BarcodeException("Invalid characters in input");
        }

        /* Add leading zeros as required */
        localstr = "";
        for (i = content.length(); i < 8; i++) {
            localstr += "0";
        }
        localstr += content;

        /* Calculate the check digit */
        checksum = 0;
        checkpart = 0;
        for (i = 0; i < 4; i++) {
            checkpart = Character.getNumericValue(localstr.charAt(i * 2));
            checksum += checkpart;
            checkpart = 2 * Character.getNumericValue(localstr.charAt((i * 2) + 1));
            if (checkpart >= 10) {
                checksum += (checkpart - 10) + 1;
            } else {
                checksum += checkpart;
            }
        }

        /* Add check digit to data string */
        checkdigit = checksum % 10;
        char check = (char) (checkdigit + '0');
        localstr += check;
        infoLine("Check Digit: " + check);

        /* Convert string into an integer value */
        pharmacode = 0;
        for (i = 0; i < localstr.length(); i++) {
            pharmacode *= 10;
            pharmacode += Character.getNumericValue(localstr.charAt(i));
        }

        /* Convert from decimal to base-32 */
        devisor = 33554432;
        for (i = 5; i >= 0; i--) {
            codeword[i] = pharmacode / devisor;
            remainder = pharmacode % devisor;
            pharmacode = remainder;
            devisor /= 32;
        }

        /* Look up values in 'Tabella di conversione' */
        risultante = "";
        for (i = 5; i >= 0; i--) {
            risultante += TABLE[codeword[i]];
        }

        /* Plot the barcode using Code 39 */

        readable = "A" + localstr;
        pattern = new String[1];
        row_count = 1;
        row_height = new int[]{-1};
        infoLine("Code 39 Equivalent: " + risultante);

        c39.setContent(risultante);
        pattern[0] = c39.pattern[0];
    }
}
