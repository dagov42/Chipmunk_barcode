package ru.chipmunkbarcode.barcodeTypes;

import ru.chipmunkbarcode.exceptions.BarcodeException;

import static ru.chipmunkbarcode.util.Arrays.positionOf;

/**
 * <p>Implements Codabar barcode symbology according to BS EN 798:1996.
 *
 * <p>Also known as NW-7, Monarch, ABC Codabar, USD-4, Ames Code and Code 27.
 * Codabar can encode any length string starting and ending with the letters
 * A-D and containing between these letters the numbers 0-9, dash (-), dollar
 * ($), colon (:), slash (/), full stop (.) or plus (+). No check digit is
 * generated.
 */
public class Codabar extends Symbol {

    private static final String[] CODABAR_TABLE = {
            "11111221", "11112211", "11121121", "22111111", "11211211",
            "21111211", "12111121", "12112111", "12211111", "21121111",
            "11122111", "11221111", "21112121", "21211121", "21212111",
            "11212121", "11221211", "12121121", "11121221", "11122211"
    };

    private static final char[] CHARACTER_SET = {
            '0', '1', '2', '3', '4',
            '5', '6', '7', '8', '9',
            '-', '$', ':', '/', '.',
            '+', 'A', 'B', 'C', 'D'
    };

    /**
     * Ratio of wide bar width to narrow bar width.
     */
    private double moduleWidthRatio = 2;

    /**
     * Sets the ratio of wide bar width to narrow bar width. Valid values are usually
     * between {@code 2} and {@code 3}. The default value is {@code 2}.
     *
     * @param moduleWidthRatio the ratio of wide bar width to narrow bar width
     */
    public void setModuleWidthRatio(double moduleWidthRatio) {
        this.moduleWidthRatio = moduleWidthRatio;
    }

    /**
     * Returns the ratio of wide bar width to narrow bar width.
     *
     * @return the ratio of wide bar width to narrow bar width
     */
    public double getModuleWidthRatio() {
        return moduleWidthRatio;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void encode() {

        if (!content.matches("[A-D]{1}[0-9:/\\$\\.\\+\u002D]+[A-D]{1}")) {
            throw new BarcodeException("Invalid characters in input");
        }

        String horizontalSpacing = "";

        int l = content.length();
        for (int i = 0; i < l; i++) {
            horizontalSpacing += CODABAR_TABLE[positionOf(content.charAt(i), CHARACTER_SET)];
        }

        readable = content;
        pattern = new String[]{horizontalSpacing};
        row_count = 1;
        rowHeight = new int[]{-1};
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected double getModuleWidth(int originalWidth) {
        if (originalWidth == 1) {
            return 1;
        } else {
            return moduleWidthRatio;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected int[] getCodewords() {
        return getPatternAsCodewords(8);
    }
}
