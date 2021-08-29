package ru.chipmunkbarcode.barcodeTypes;

import ru.chipmunkbarcode.exceptions.BarcodeException;

/**
 * <p>Implements Korea Post Barcode. Input should consist of of a six-digit number. A Modulo-10
 * check digit is calculated and added, and should not form part of the input data.
 */
public class KoreaPost extends Symbol {

    private static final String[] KOREA_TABLE = {
            "1313150613", "0713131313", "0417131313", "1506131313", "0413171313",
            "17171313", "1315061313", "0413131713", "17131713", "13171713"
    };

    @Override
    protected void encode() {

        if (!content.matches("[0-9]+")) {
            throw new BarcodeException("Invalid characters in input");
        }

        if (content.length() > 6) {
            throw new BarcodeException("Input data too long");
        }

        String padded = "";
        for (int i = 0; i < (6 - content.length()); i++) {
            padded += "0";
        }
        padded += content;

        int total = 0;
        String accumulator = "";
        for (int i = 0; i < padded.length(); i++) {
            int j = Character.getNumericValue(padded.charAt(i));
            accumulator += KOREA_TABLE[j];
            total += j;
        }

        int checkd = 10 - (total % 10);
        if (checkd == 10) {
            checkd = 0;
        }
        infoLine("Check Digit: " + checkd);
        accumulator += KOREA_TABLE[checkd];

        readable = padded + checkd;
        pattern = new String[]{accumulator};
        row_count = 1;
        row_height = new int[]{-1};
    }
}
