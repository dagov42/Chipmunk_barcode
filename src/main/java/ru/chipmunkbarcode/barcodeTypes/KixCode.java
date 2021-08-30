package ru.chipmunkbarcode.barcodeTypes;

import ru.chipmunkbarcode.exceptions.BarcodeException;

import java.awt.geom.Rectangle2D;
import java.util.Locale;

import static ru.chipmunkbarcode.util.Arrays.positionOf;

/**
 * <p>Implements Dutch Post KIX Code as used by Royal Dutch TPG Post (Netherlands).
 *
 * <p>The input data can consist of digits 0-9 and characters A-Z, and should be 11
 * characters in length. No check digit is added.
 *
 * <p>KIX Code is the same as RM4SCC, but without the check digit.
 *
 * @see <a href="http://www.tntpost.nl/zakelijk/klantenservice/downloads/kIX_code/download.aspx">KIX Code Specification</a>
 */
public class KixCode extends Symbol {

    private static final String[] ROYAL_TABLE = {
        "TTFF", "TDAF", "TDFA", "DTAF", "DTFA", "DDAA", "TADF", "TFTF", "TFDA",
        "DATF", "DADA", "DFTA", "TAFD", "TFAD", "TFFT", "DAAD", "DAFT", "DFAT",
        "ATDF", "ADTF", "ADDA", "FTTF", "FTDA", "FDTA", "ATFD", "ADAD", "ADFT",
        "FTAD", "FTFT", "FDAT", "AADD", "AFTD", "AFDT", "FATD", "FADT", "FFTT"
    };

    private static final char[] KR_SET = {
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D',
        'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R',
        'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'
    };

    @Override
    protected void encode() {

        content = content.toUpperCase(Locale.ENGLISH);

        if(!content.matches("[0-9A-Z]+")) {
            throw new BarcodeException("Invalid characters in data");
        }

        StringBuilder sb = new StringBuilder(content.length());
        for (int i = 0; i < content.length(); i++) {
            int j = positionOf(content.charAt(i), KR_SET);
            sb.append(ROYAL_TABLE[j]);
        }

        String dest = sb.toString();
        infoLine("Encoding: " + dest);

        readable = "";
        pattern = new String[] { dest };
        row_count = 1;
        row_height = new int[] { -1 };
    }

    @Override
    protected void plotSymbol() {
        int xBlock;
        int x, y, w, h;

        rectangles.clear();
        x = 0;
        w = 1;
        y = 0;
        h = 0;
        for (xBlock = 0; xBlock < pattern[0].length(); xBlock++) {
            char c = pattern[0].charAt(xBlock);
            switch (c) {
                case 'A':
                    y = 0;
                    h = 5;
                    break;
                case 'D':
                    y = 3;
                    h = 5;
                    break;
                case 'F':
                    y = 0;
                    h = 8;
                    break;
                case 'T':
                    y = 3;
                    h = 2;
                    break;
                default:
                    throw new IllegalStateException("Unknown pattern character: " + c);
            }
            rectangles.add(new Rectangle2D.Double(x, y, w, h));
            x += 2;
        }
        symbol_width = ((pattern[0].length() - 1) * 2) + 1; // final bar doesn't need extra whitespace
        symbol_height = 8;
    }
}
