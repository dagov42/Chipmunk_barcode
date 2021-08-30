package ru.chipmunkbarcode.barcodeTypes;

import ru.chipmunkbarcode.exceptions.BarcodeException;

/**
 * <p>Implements Code 3 of 9 Extended, also known as Code 39e and Code39+.
 *
 * <p>Supports encoding of all characters in the 7-bit ASCII table. A modulo-43
 * check digit can be added if required.
 */
public class Code3Of9Extended extends Symbol {

    private static final String[] E_CODE_39 = {
            "%U", "$A", "$B", "$C", "$D", "$E", "$F", "$G", "$H", "$I", "$J", "$K",
            "$L", "$M", "$N", "$O", "$P", "$Q", "$R", "$S", "$T", "$U", "$V", "$W",
            "$X", "$Y", "$Z", "%A", "%B", "%C", "%D", "%E", " ", "/A", "/B", "/C",
            "/D", "/E", "/F", "/G", "/H", "/I", "/J", "/K", "/L", "-", ".", "/O",
            "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "/Z", "%F", "%G",
            "%H", "%I", "%J", "%V", "A", "B", "C", "D", "E", "F", "G", "H", "I",
            "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W",
            "X", "Y", "Z", "%K", "%L", "%M", "%N", "%O", "%W", "+A", "+B", "+C",
            "+D", "+E", "+F", "+G", "+H", "+I", "+J", "+K", "+L", "+M", "+N", "+O",
            "+P", "+Q", "+R", "+S", "+T", "+U", "+V", "+W", "+X", "+Y", "+Z", "%P",
            "%Q", "%R", "%S", "%T"
    };

    public enum CheckDigit {
        NONE, MOD43
    }

    private CheckDigit checkOption = CheckDigit.NONE;

    /**
     * Select addition of optional Modulo-43 check digit or encoding without check digit.
     *
     * @param checkMode check digit option
     */
    public void setCheckDigit(CheckDigit checkMode) {
        checkOption = checkMode;
    }

    @Override
    protected void encode() {
        String buffer = "";
        int l = content.length();
        int asciicode;
        Code3Of9 c = new Code3Of9();

        if (checkOption == CheckDigit.MOD43) {
            c.setCheckDigit(Code3Of9.CheckDigit.MOD43);
        }

        if (!content.matches("[\u0000-\u007F]+")) {
            throw new BarcodeException("Invalid characters in input data");
        }

        for (int i = 0; i < l; i++) {
            asciicode = content.charAt(i);
            buffer += E_CODE_39[asciicode];
        }

        c.setContent(buffer);

        readable = content;
        pattern = new String[1];
        pattern[0] = c.pattern[0];
        row_count = 1;
        row_height = new int[1];
        row_height[0] = -1;
    }
}
