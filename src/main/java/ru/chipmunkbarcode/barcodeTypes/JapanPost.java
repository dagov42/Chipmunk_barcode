package ru.chipmunkbarcode.barcodeTypes;

import ru.chipmunkbarcode.exceptions.BarcodeException;

import java.awt.geom.Rectangle2D;
import java.util.Locale;

import static ru.chipmunkbarcode.util.Arrays.positionOf;

/**
 * <p>Implements the Japanese Postal Code symbology as used to encode address
 * data for mail items in Japan. Valid input characters are digits 0-9,
 * characters A-Z and the dash (-) character. A modulo-19 check digit is
 * added and should not be included in the input data.
 */
public class JapanPost extends Symbol {

    private static final String[] JAPAN_TABLE = {
            "FFT", "FDA", "DFA", "FAD", "FTF", "DAF", "AFD", "ADF", "TFF", "FTT",
            "TFT", "DAT", "DTA", "ADT", "TDA", "ATD", "TAD", "TTF", "FFF"
    };

    private static final char[] KASUT_SET = {
            '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '-', 'a', 'b', 'c',
            'd', 'e', 'f', 'g', 'h'
    };

    private static final char[] CH_KASUT_SET = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '-', 'a', 'b', 'c',
            'd', 'e', 'f', 'g', 'h'
    };

    @Override
    protected void encode() {
        String dest;
        String inter;
        int i, sum, check;
        char c;

        content = content.toUpperCase(Locale.ENGLISH);
        if (!content.matches("[0-9A-Z\\-]+")) {
            throw new BarcodeException("Invalid characters in data");
        }

        inter = "";

        for (i = 0;
             (i < content.length()) && (inter.length() < 20); i++) {
            c = content.charAt(i);

            if ((c >= '0') && (c <= '9')) {
                inter += c;
            }
            if (c == '-') {
                inter += c;
            }
            if ((c >= 'A') && (c <= 'J')) {
                inter += 'a';
                inter += CH_KASUT_SET[(c - 'A')];
            }

            if ((c >= 'K') && (c <= 'O')) {
                inter += 'b';
                inter += CH_KASUT_SET[(c - 'K')];
            }

            if ((c >= 'U') && (c <= 'Z')) {
                inter += 'c';
                inter += CH_KASUT_SET[(c - 'U')];
            }
        }

        for (i = inter.length(); i < 20; i++) {
            inter += "d";
        }

        dest = "FD";

        sum = 0;
        for (i = 0; i < 20; i++) {
            dest += JAPAN_TABLE[positionOf(inter.charAt(i), KASUT_SET)];
            sum += positionOf(inter.charAt(i), CH_KASUT_SET);
        }

        /* Calculate check digit */
        check = 19 - (sum % 19);
        if (check == 19) {
            check = 0;
        }
        dest += JAPAN_TABLE[positionOf(CH_KASUT_SET[check], KASUT_SET)];
        dest += "DF";

        infoLine("Encoding: " + dest);
        infoLine("Check Digit: " + check);

        readable = "";
        pattern = new String[]{dest};
        row_count = 1;
        row_height = new int[]{-1};
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
            switch (pattern[0].charAt(xBlock)) {
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
            }

            Rectangle2D.Double rect = new Rectangle2D.Double(x, y, w, h);
            rectangles.add(rect);

            x += 2;
        }
        symbol_width = pattern[0].length() * 3;
        symbol_height = 8;
    }
}
