package ru.chipmunkbarcode.barcodeTypes;

import ru.chipmunkbarcode.exceptions.BarcodeException;

import java.awt.geom.Rectangle2D;

/**
 * <p>Implements USPS Intelligent Mail Package Barcode (IMpb), a linear barcode based on GS1-128.
 * Includes additional data checks.
 *
 * @see <a href="https://ribbs.usps.gov/intelligentmail_package/documents/tech_guides/BarcodePackageIMSpec.pdf">IMpb Specification</a>
 */
public class UspsPackage extends Symbol {

    @Override
    protected void encode() {

        if (!content.matches("[0-9\\[\\]]+")) {
            /* Input must be numeric only */
            throw new BarcodeException("Invalid IMpb data");
        }

        if (content.length() % 2 != 0) {
            /* Input must be even length */
            throw new BarcodeException("Invalid IMpb data");
        }

        Code128 code128 = new Code128();
        code128.unsetCc();
        code128.setDataType(DataType.GS1);
        code128.setContent(content);

        boolean fourTwenty = content.length() > 4 &&
                content.charAt(1) == '4' &&
                content.charAt(2) == '2' &&
                content.charAt(3) == '0';

        StringBuilder hrt = new StringBuilder();
        int bracketCount = 0;
        for (int i = 0; i < content.length(); i++) {
            if (content.charAt(i) == '[') {
                bracketCount++;
            }
            if (!(fourTwenty && bracketCount < 2)) {
                if ((content.charAt(i) >= '0') && (content.charAt(i) <= '9')) {
                    hrt.append(content.charAt(i));
                }
            }
        }

        StringBuilder spacedHrt = new StringBuilder();
        for (int i = 0; i < hrt.length(); i++) {
            spacedHrt.append(hrt.charAt(i));
            if (i % 4 == 3) {
                spacedHrt.append(" ");
            }
        }

        encodeInfo = code128.encodeInfo;
        readable = spacedHrt.toString();
        pattern = new String[1];
        pattern[0] = code128.pattern[0];
        row_count = 1;
        row_height = new int[1];
        row_height[0] = -1;
    }

    @Override
    protected void plotSymbol() {
        int xBlock;
        int x, y, w, h;
        boolean black;
        int offset = 20;
        int yoffset = 15;
        String banner = "USPS TRACKING #";

        rectangles.clear();
        texts.clear();
        y = yoffset;
        h = 0;
        black = true;
        x = 0;
        for (xBlock = 0; xBlock < pattern[0].length(); xBlock++) {
            w = pattern[0].charAt(xBlock) - '0';
            if (black) {
                if (row_height[0] == -1) {
                    h = default_height;
                } else {
                    h = row_height[0];
                }
                if (w != 0 && h != 0) {
                    Rectangle2D.Double rect = new Rectangle2D.Double(x + offset, y, w, h);
                    rectangles.add(rect);
                }
                symbol_width = x + w + (2 * offset);
            }
            black = !black;
            x += w;
        }
        symbol_height = h + (2 * yoffset);

        // Add boundary bars
        Rectangle2D.Double topBar = new Rectangle2D.Double(0, 0, symbol_width, 2);
        Rectangle2D.Double bottomBar = new Rectangle2D.Double(0, symbol_height - 2, symbol_width, 2);
        rectangles.add(topBar);
        rectangles.add(bottomBar);

        texts.add(new TextBox(0, symbol_height - 6.0, symbol_width, readable, humanReadableAlignment));
        texts.add(new TextBox(0, 12.0, symbol_width, banner, humanReadableAlignment));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected int[] getCodewords() {
        return getPatternAsCodewords(6);
    }
}
