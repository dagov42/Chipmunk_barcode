package ru.chipmunkbarcode.barcodeTypes;

import ru.chipmunkbarcode.exceptions.BarcodeException;

/**
 * <p>Implements the MSI (Modified Plessey) bar code symbology.
 *
 * <p>MSI Plessey can encode a string of numeric digits and has a range of check digit options.
 */
public class MsiPlessey extends Symbol {

    public enum CheckDigit {
        NONE, MOD10, MOD10_MOD10, MOD11, MOD11_MOD10
    }

    private final static String[] MSI_PLESS_TABLE = {
        "12121212", "12121221", "12122112", "12122121", "12211212", "12211221",
        "12212112", "12212121", "21121212", "21121221"
    };

    private CheckDigit checkDigit = CheckDigit.NONE;

    /**
     * Set the check digit scheme to use. Options are: None, Modulo-10,
     * 2 x Modulo-10, Modulo-11 and Modulo-11 &amp; 10.
     *
     * @param checkDigit the type of check digit to add to symbol
     */
    public void setCheckDigit(CheckDigit checkDigit) {
        this.checkDigit = checkDigit;
    }

    /**
     * Returns the check digit scheme being used.
     *
     * @return the check digit scheme being used
     */
    public CheckDigit getCheckDigit() {
        return checkDigit;
    }

    @Override
    protected void encode() {

        String intermediate;
        int length = content.length();
        int i;
        String evenString;
        String oddString;
        String addupString;
        int spacer;
        int addup;
        int weight;
        int checkDigit1;
        int checkDigit2;

        if (!content.matches("[0-9]+")) {
            throw new BarcodeException("Invalid characters in input");
        }

        intermediate = "21"; // Start
        for (i = 0; i < length; i++) {
            intermediate += MSI_PLESS_TABLE[Character.getNumericValue(content.charAt(i))];
        }

        readable = content;

        if (checkDigit == CheckDigit.MOD10 || checkDigit == CheckDigit.MOD10_MOD10) {
            /* Add Modulo-10 check digit */
            evenString = "";
            oddString = "";

            spacer = content.length() & 1;

            for (i = content.length() - 1; i >= 0; i--) {
                if (spacer == 1) {
                    if ((i & 1) != 0) {
                        evenString = content.charAt(i) + evenString;
                    } else {
                        oddString = content.charAt(i) + oddString;
                    }
                } else {
                    if ((i & 1) != 0) {
                        oddString = content.charAt(i) + oddString;
                    } else {
                        evenString = content.charAt(i) + evenString;
                    }
                }
            }

            if (oddString.length() == 0) {
                addupString = "0";
            } else {
                addupString = Integer.toString(Integer.parseInt(oddString) * 2);
            }

            addupString += evenString;

            addup = 0;
            for(i = 0; i < addupString.length(); i++) {
                addup += addupString.charAt(i) - '0';
            }

            checkDigit1 = 10 - (addup % 10);
            if (checkDigit1 == 10) {
                checkDigit1 = 0;
            }

            intermediate += MSI_PLESS_TABLE[checkDigit1];
            readable += checkDigit1;
        }

        if (checkDigit == CheckDigit.MOD11 || checkDigit == CheckDigit.MOD11_MOD10) {
            /* Add a Modulo-11 check digit */
            weight = 2;
            addup = 0;
            for (i = content.length() - 1; i >= 0; i--) {
                addup += (content.charAt(i) - '0') * weight;
                weight++;

                if (weight == 8) {
                    weight = 2;
                }
            }

            checkDigit1 = 11 - (addup % 11);

            if (checkDigit1 == 11) {
                checkDigit1 = 0;
            }

            readable += checkDigit1;
            if (checkDigit1 == 10) {
                intermediate += MSI_PLESS_TABLE[1];
                intermediate += MSI_PLESS_TABLE[0];
            } else {
                intermediate += MSI_PLESS_TABLE[checkDigit1];
            }
        }

        if (checkDigit == CheckDigit.MOD10_MOD10 || checkDigit == CheckDigit.MOD11_MOD10) {
            /* Add a second Modulo-10 check digit */
            evenString = "";
            oddString = "";

            spacer = readable.length() & 1;

            for (i = readable.length() - 1; i >= 0; i--) {
                if (spacer == 1) {
                    if ((i & 1) != 0) {
                        evenString = readable.charAt(i) + evenString;
                    } else {
                        oddString = readable.charAt(i) + oddString;
                    }
                } else {
                    if ((i & 1) != 0) {
                        oddString = readable.charAt(i) + oddString;
                    } else {
                        evenString = readable.charAt(i) + evenString;
                    }
                }
            }

            if(oddString.length() == 0) {
                addupString = "0";
            } else {
                addupString = Integer.toString(Integer.parseInt(oddString) * 2);
            }

            addupString += evenString;

            addup = 0;
            for(i = 0; i < addupString.length(); i++) {
                addup += addupString.charAt(i) - '0';
            }

            checkDigit2 = 10 - (addup % 10);
            if (checkDigit2 == 10) {
                checkDigit2 = 0;
            }

            intermediate += MSI_PLESS_TABLE[checkDigit2];
            readable += checkDigit2;
        }

        intermediate += "121"; // Stop

        pattern = new String[] { intermediate };
        row_count = 1;
        rowHeight = new int[] { -1 };
    }
}
