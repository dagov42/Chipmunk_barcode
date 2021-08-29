package ru.chipmunkbarcode.barcodeTypes;

import ru.chipmunkbarcode.exceptions.BarcodeException;

import static java.nio.charset.StandardCharsets.ISO_8859_1;

/**
 * <p>Implements Code 128 bar code symbology according to ISO/IEC 15417:2007.
 *
 * <p>Code 128 supports encoding of 8-bit ISO 8859-1 (Latin-1) characters.
 *
 * <p>Setting GS1 mode allows encoding in GS1-128 (also known as UCC/EAN-128).
 */
public class Code128 extends Symbol {

    private enum Mode {
        NULL, SHIFTA, LATCHA, SHIFTB, LATCHB, SHIFTC, LATCHC, AORB, ABORC
    }

    private enum FMode {
        SHIFTN, LATCHN, SHIFTF, LATCHF
    }

    private enum Composite {
        OFF, CCA, CCB, CCC
    }

    protected static final String[] CODE128_TABLE = {
            "212222", "222122", "222221", "121223", "121322", "131222", "122213",
            "122312", "132212", "221213", "221312", "231212", "112232", "122132",
            "122231", "113222", "123122", "123221", "223211", "221132", "221231",
            "213212", "223112", "312131", "311222", "321122", "321221", "312212",
            "322112", "322211", "212123", "212321", "232121", "111323", "131123",
            "131321", "112313", "132113", "132311", "211313", "231113", "231311",
            "112133", "112331", "132131", "113123", "113321", "133121", "313121",
            "211331", "231131", "213113", "213311", "213131", "311123", "311321",
            "331121", "312113", "312311", "332111", "314111", "221411", "431111",
            "111224", "111422", "121124", "121421", "141122", "141221", "112214",
            "112412", "122114", "122411", "142112", "142211", "241211", "221114",
            "413111", "241112", "134111", "111242", "121142", "121241", "114212",
            "124112", "124211", "411212", "421112", "421211", "212141", "214121",
            "412121", "111143", "111341", "131141", "114113", "114311", "411113",
            "411311", "113141", "114131", "311141", "411131", "211412", "211214",
            "211232", "2331112"
    };

    private boolean suppressModeC = false;
    private Composite compositeMode = Composite.OFF;

    /**
     * Optionally prevents this symbol from using subset mode C for numeric data compression.
     *
     * @param suppressModeC whether or not to prevent this symbol from using subset mode C
     */
    public void setSuppressModeC(boolean suppressModeC) {
        this.suppressModeC = suppressModeC;
    }

    /**
     * Returns whether or not this symbol is prevented from using subset mode C for numeric data compression.
     *
     * @return whether or not this symbol is prevented from using subset mode C for numeric data compression
     */
    public boolean getSuppressModeC() {
        return suppressModeC;
    }

    protected void setCca() {
        compositeMode = Composite.CCA;
    }

    protected void setCcb() {
        compositeMode = Composite.CCB;
    }

    protected void setCcc() {
        compositeMode = Composite.CCC;
    }

    public void unsetCc() {
        compositeMode = Composite.OFF;
    }

    @Override
    protected boolean gs1Supported() {
        return true;
    }

    @Override
    protected void encode() {
        int i, j, k;
        int inputPoint = 0;
        Mode mode, lastMode;
        Mode lastSet, currentSet;
        double glyphCount;
        int barCharacters = 0, totalSum = 0;
        FMode fState = FMode.LATCHN;
        Mode[] modeType = new Mode[200];
        int[] modeLength = new int[200];
        int[] values = new int[200];
        int c;
        int linkageFlag = 0;
        int indexPoint = 0;
        int read = 0;

        inputData = toBytes(content, ISO_8859_1);
        if (inputData == null) {
            throw new BarcodeException("Invalid characters in input data");
        }

        int sourceLength = inputData.length;

        FMode[] fset = new FMode[200];
        Mode[] set = new Mode[200]; /* set[] = Calculated mode for each character */

        if (sourceLength > 170) {
            throw new BarcodeException("Input data too long");
        }

        /* Detect extended ASCII characters */
        for (i = 0; i < sourceLength; i++) {
            int ch = inputData[i];
            if (ch >= 128 && ch != FNC1 && ch != FNC2 && ch != FNC3 && ch != FNC4) {
                fset[i] = FMode.SHIFTF;
            } else {
                fset[i] = FMode.LATCHN;
            }
        }

        /* Decide when to latch to extended mode - Annex E note 3 */
        j = 0;
        for (i = 0; i < sourceLength; i++) {
            if (fset[i] == FMode.SHIFTF) {
                j++;
            } else {
                j = 0;
            }
            if (j >= 5) {
                for (k = i; k > (i - 5); k--) {
                    fset[k] = FMode.LATCHF;
                }
            }
            if ((j >= 3) && (i == (sourceLength - 1))) {
                for (k = i; k > (i - 3); k--) {
                    fset[k] = FMode.LATCHF;
                }
            }
        }

        /* Decide if it is worth reverting to 646 encodation for a few characters as described in 4.3.4.2 (d) */
        for (i = 1; i < sourceLength; i++) {
            if ((fset[i - 1] == FMode.LATCHF) && (fset[i] == FMode.LATCHN)) {
                /* Detected a change from 8859-1 to 646 - count how long for */
                if ((j < 5) || ((j < 3) && ((i + j) == (sourceLength - 1)))) {
                    /* Uses the same figures recommended by Annex E note 3 */
                    /* Change to shifting back rather than latching back */
                    for (k = 0; k < j; k++) {
                        fset[i + k] = FMode.SHIFTN;
                    }
                }
            }
        }

        /* Decide on mode using same system as PDF417 and rules of ISO 15417 Annex E */
        int letter = inputData[inputPoint];
        int numbers = (letter >= '0' && letter <= '9' ? 1 : 0);
        mode = findSubset(letter, numbers);
        modeType[0] = mode;
        modeLength[0] += length(letter, mode);
        for (i = 1; i < sourceLength; i++) {
            letter = inputData[i];
            lastMode = mode;
            mode = findSubset(letter, numbers);
            if (mode == lastMode) {
                modeLength[indexPoint] += length(letter, mode);
            } else {
                indexPoint++;
                modeType[indexPoint] = mode;
                modeLength[indexPoint] = length(letter, mode);
            }
            if (letter >= '0' && letter <= '9') {
                numbers++;
            } else {
                numbers = 0;
            }
        }
        indexPoint++;
        indexPoint = reduceSubsetChanges(modeType, modeLength, indexPoint);

        /* Put set data into set[] */
        read = 0;
        for (i = 0; i < indexPoint; i++) {
            for (j = 0; j < modeLength[i]; j++) {
                set[read] = modeType[i];
                read++;
            }
        }

        /* Resolve odd length LATCHC blocks */
        int cs = 0, nums = 0, fncs = 0;
        for (i = 0; i < read; i++) {
            if (set[i] == Mode.LATCHC) {
                cs++;
                if (inputData[i] >= '0' && inputData[i] <= '9') {
                    nums++;
                } else if (inputData[i] == FNC1) {
                    fncs++;
                }
            } else {
                resolveOddCs(set, i, cs, nums, fncs);
                cs = 0;
                nums = 0;
                fncs = 0;
            }
        }
        resolveOddCs(set, i, cs, nums, fncs);

        /* Adjust for strings which start with shift characters - make them latch instead */
        if (set[0] == Mode.SHIFTA) {
            i = 0;
            do {
                set[i] = Mode.LATCHA;
                i++;
            } while (set[i] == Mode.SHIFTA);
        }
        if (set[0] == Mode.SHIFTB) {
            i = 0;
            do {
                set[i] = Mode.LATCHB;
                i++;
            } while (set[i] == Mode.SHIFTB);
        }

        /* Now we can calculate how long the barcode is going to be - and stop it from being too long */
        lastSet = Mode.NULL;
        glyphCount = 0.0;
        for (i = 0; i < sourceLength; i++) {
            if ((set[i] == Mode.SHIFTA) || (set[i] == Mode.SHIFTB)) {
                glyphCount += 1.0;
            }
            if ((fset[i] == FMode.SHIFTF) || (fset[i] == FMode.SHIFTN)) {
                glyphCount += 1.0;
            }
            if (((set[i] == Mode.LATCHA) || (set[i] == Mode.LATCHB)) || (set[i] == Mode.LATCHC)) {
                if (set[i] != lastSet) {
                    lastSet = set[i];
                    glyphCount += 1.0;
                }
            }
            if (i == 0) {
                if (fset[i] == FMode.LATCHF) {
                    glyphCount += 2.0;
                }
            } else {
                if ((fset[i] == FMode.LATCHF) && (fset[i - 1] != FMode.LATCHF)) {
                    glyphCount += 2.0;
                }
                if ((fset[i] != FMode.LATCHF) && (fset[i - 1] == FMode.LATCHF)) {
                    glyphCount += 2.0;
                }
            }
            if (set[i] == Mode.LATCHC) {
                if (inputData[i] == FNC1) {
                    glyphCount += 1.0;
                } else {
                    glyphCount += 0.5;
                }
            } else {
                glyphCount += 1.0;
            }
        }
        if (glyphCount > 80.0) {
            throw new BarcodeException("Input data too long");
        }

        info("Encoding: ");

        /* So now we know what start character to use - we can get on with it! */
        if (readerInit) {
            /* Reader Initialisation mode */
            switch (set[0]) {
                case LATCHA:
                    values[0] = 103;
                    currentSet = Mode.LATCHA;
                    values[1] = 96;
                    barCharacters++;
                    info("STARTA FNC3 ");
                    break;
                case LATCHB:
                    values[0] = 104;
                    currentSet = Mode.LATCHB;
                    values[1] = 96;
                    barCharacters++;
                    info("STARTB FNC3 ");
                    break;
                default: /* Start C */
                    values[0] = 104;
                    values[1] = 96;
                    values[2] = 99;
                    barCharacters += 2;
                    currentSet = Mode.LATCHC;
                    info("STARTB FNC3 CODEC ");
                    break;
            }
        } else {
            /* Normal mode */
            switch (set[0]) {
                case LATCHA:
                    values[0] = 103;
                    currentSet = Mode.LATCHA;
                    info("STARTA ");
                    break;
                case LATCHB:
                    values[0] = 104;
                    currentSet = Mode.LATCHB;
                    info("STARTB ");
                    break;
                default:
                    values[0] = 105;
                    currentSet = Mode.LATCHC;
                    info("STARTC ");
                    break;
            }
        }
        barCharacters++;

        if (inputDataType == DataType.GS1) {
            values[1] = 102;
            barCharacters++;
            info("FNC1 ");
        }

        if (fset[0] == FMode.LATCHF) {
            switch (currentSet) {
                case LATCHA:
                    values[barCharacters] = 101;
                    values[barCharacters + 1] = 101;
                    info("FNC4 FNC4 ");
                    break;
                case LATCHB:
                    values[barCharacters] = 100;
                    values[barCharacters + 1] = 100;
                    info("FNC4 FNC4 ");
                    break;
            }
            barCharacters += 2;
            fState = FMode.LATCHF;
        }

        /* Encode the data */
        read = 0;
        do {
            if ((read != 0) && (set[read] != currentSet)) { /* Latch different code set */
                switch (set[read]) {
                    case LATCHA:
                        values[barCharacters] = 101;
                        barCharacters++;
                        currentSet = Mode.LATCHA;
                        info("CODEA ");
                        break;
                    case LATCHB:
                        values[barCharacters] = 100;
                        barCharacters++;
                        currentSet = Mode.LATCHB;
                        info("CODEB ");
                        break;
                    case LATCHC:
                        values[barCharacters] = 99;
                        barCharacters++;
                        currentSet = Mode.LATCHC;
                        info("CODEC ");
                        break;
                }
            }

            if (read != 0) {
                if ((fset[read] == FMode.LATCHF) && (fState == FMode.LATCHN)) {
                    /* Latch beginning of extended mode */
                    switch (currentSet) {
                        case LATCHA:
                            values[barCharacters] = 101;
                            values[barCharacters + 1] = 101;
                            info("FNC4 FNC4 ");
                            break;
                        case LATCHB:
                            values[barCharacters] = 100;
                            values[barCharacters + 1] = 100;
                            info("FNC4 FNC4 ");
                            break;
                    }
                    barCharacters += 2;
                    fState = FMode.LATCHF;
                }
                if ((fset[read] == FMode.LATCHN) && (fState == FMode.LATCHF)) {
                    /* Latch end of extended mode */
                    switch (currentSet) {
                        case LATCHA:
                            values[barCharacters] = 101;
                            values[barCharacters + 1] = 101;
                            info("FNC4 FNC4 ");
                            break;
                        case LATCHB:
                            values[barCharacters] = 100;
                            values[barCharacters + 1] = 100;
                            info("FNC4 FNC4 ");
                            break;
                    }
                    barCharacters += 2;
                    fState = FMode.LATCHN;
                }
            }

            if ((fset[read] == FMode.SHIFTF) || (fset[read] == FMode.SHIFTN)) {
                /* Shift to or from extended mode */
                switch (currentSet) {
                    case LATCHA:
                        values[barCharacters] = 101;
                        info("FNC4 ");
                        break;
                    case LATCHB:
                        values[barCharacters] = 100;
                        info("FNC4 ");
                        break;
                }
                barCharacters++;
            }

            if ((set[read] == Mode.SHIFTA) || (set[read] == Mode.SHIFTB)) {
                /* Insert shift character */
                values[barCharacters] = 98;
                info("SHFT ");
                barCharacters++;
            }

            /* Encode data characters */
            c = inputData[read];
            switch (set[read]) {
                case SHIFTA:
                case LATCHA:
                    if (c == FNC1) {
                        values[barCharacters] = 102;
                        info("FNC1 ");
                    } else if (c == FNC2) {
                        values[barCharacters] = 97;
                        info("FNC2 ");
                    } else if (c == FNC3) {
                        values[barCharacters] = 96;
                        info("FNC3 ");
                    } else if (c == FNC4) {
                        values[barCharacters] = 101;
                        info("FNC4 ");
                    } else if (c > 127) {
                        if (c < 160) {
                            values[barCharacters] = (c - 128) + 64;
                        } else {
                            values[barCharacters] = (c - 128) - 32;
                        }
                        infoSpace(values[barCharacters]);
                    } else {
                        if (c < 32) {
                            values[barCharacters] = c + 64;
                        } else {
                            values[barCharacters] = c - 32;
                        }
                        infoSpace(values[barCharacters]);
                    }
                    barCharacters++;
                    read++;
                    break;
                case SHIFTB:
                case LATCHB:
                    if (c == FNC1) {
                        values[barCharacters] = 102;
                        info("FNC1 ");
                    } else if (c == FNC2) {
                        values[barCharacters] = 97;
                        info("FNC2 ");
                    } else if (c == FNC3) {
                        values[barCharacters] = 96;
                        info("FNC3 ");
                    } else if (c == FNC4) {
                        values[barCharacters] = 100;
                        info("FNC4 ");
                    } else if (c > 127) {
                        values[barCharacters] = c - 32 - 128;
                        infoSpace(values[barCharacters]);
                    } else {
                        values[barCharacters] = c - 32;
                        infoSpace(values[barCharacters]);
                    }
                    barCharacters++;
                    read++;
                    break;
                case LATCHC:
                    if (c == FNC1) {
                        values[barCharacters] = 102;
                        info("FNC1 ");
                        barCharacters++;
                        read++;
                    } else {
                        int d = inputData[read + 1];
                        int weight = (10 * (c - '0')) + (d - '0');
                        values[barCharacters] = weight;
                        infoSpace(values[barCharacters]);
                        barCharacters++;
                        read += 2;
                    }
                    break;
            }

        } while (read < sourceLength);

        infoLine();

        /* "...note that the linkage flag is an extra code set character between
        the last data character and the Symbol Check Character" (GS1 Specification) */

        /* Linkage flags in GS1-128 are determined by ISO/IEC 24723 section 7.4 */

        switch (compositeMode) {
            case CCA:
            case CCB:
                /* CC-A or CC-B 2D component */
                switch (set[sourceLength - 1]) {
                    case LATCHA:
                        linkageFlag = 100;
                        break;
                    case LATCHB:
                        linkageFlag = 99;
                        break;
                    case LATCHC:
                        linkageFlag = 101;
                        break;
                }
                infoLine("Linkage Flag: " + linkageFlag);
                break;
            case CCC:
                /* CC-C 2D component */
                switch (set[sourceLength - 1]) {
                    case LATCHA:
                        linkageFlag = 99;
                        break;
                    case LATCHB:
                        linkageFlag = 101;
                        break;
                    case LATCHC:
                        linkageFlag = 100;
                        break;
                }
                infoLine("Linkage Flag: " + linkageFlag);
                break;
            default:
                break;
        }

        if (linkageFlag != 0) {
            values[barCharacters] = linkageFlag;
            barCharacters++;
        }

        infoLine("Data Codewords: " + barCharacters);

        /* Check digit calculation */
        for (i = 0; i < barCharacters; i++) {
            totalSum += (i == 0 ? values[i] : values[i] * i);
        }
        int checkDigit = totalSum % 103;
        infoLine("Check Digit: " + checkDigit);

        /* Build pattern string */
        StringBuilder dest = new StringBuilder((6 * barCharacters) + 6 + 7);
        for (i = 0; i < barCharacters; i++) {
            dest.append(CODE128_TABLE[values[i]]);
        }
        dest.append(CODE128_TABLE[checkDigit]);
        dest.append(CODE128_TABLE[106]); // stop character

        /* Readable text */
        if (inputDataType != DataType.GS1) {
            readable = removeFncEscapeSequences(content);
            if (inputDataType == DataType.HIBC) {
                readable = "*" + readable + "*";
            }
        }

        if (compositeMode == Composite.OFF) {
            pattern = new String[]{dest.toString()};
            row_height = new int[]{-1};
            row_count = 1;
        } else {
            /* Add the separator pattern for composite symbols */
            pattern = new String[]{"0" + dest, dest.toString()};
            row_height = new int[]{1, -1};
            row_count = 2;
        }
    }

    private static String removeFncEscapeSequences(String s) {
        return s.replace(FNC1_STRING, "")
                .replace(FNC2_STRING, "")
                .replace(FNC3_STRING, "")
                .replace(FNC4_STRING, "");
    }

    private void resolveOddCs(Mode[] set, int i, int cs, int nums, int fncs) {
        if ((nums & 1) != 0) {
            int index;
            Mode m;
            if (i - cs == 0 || fncs > 0) {
                // Rule 2: first block -> swap last digit to A or B
                index = i - 1;
                if (index + 1 < set.length && set[index + 1] != null && set[index + 1] != Mode.LATCHC) {
                    // next block is either A or B -- match it
                    m = set[index + 1];
                } else {
                    // next block is C, or there is no next block -- just latch to B
                    m = Mode.LATCHB;
                }
            } else {
                // Rule 3b: subsequent block -> swap first digit to A or B
                // Note that we make an exception for C blocks which contain one (or more) FNC1 characters,
                // since swapping the first digit would place the FNC1 in an invalid position in the block
                index = i - nums;
                if (index - 1 >= 0 && set[index - 1] != null && set[index - 1] != Mode.LATCHC) {
                    // previous block is either A or B -- match it
                    m = set[index - 1];
                } else {
                    // previous block is C, or there is no previous block -- just latch to B
                    m = Mode.LATCHB;
                }
            }
            set[index] = m;
        }
    }

    private Mode findSubset(int letter, int numbers) {
        Mode mode;
        if (letter == FNC1) {
            if (numbers % 2 == 0) {
                /* ISO 15417 Annex E Note 2 */
                /* FNC1 may use subset C, so long as it doesn't break data into an odd number of digits */
                mode = Mode.ABORC;
            } else {
                mode = Mode.AORB;
            }
        } else if (letter == FNC2 || letter == FNC3 || letter == FNC4) {
            mode = Mode.AORB;
        } else if (letter <= 31) {
            mode = Mode.SHIFTA;
        } else if ((letter >= 48) && (letter <= 57)) {
            mode = Mode.ABORC;
        } else if (letter <= 95) {
            mode = Mode.AORB;
        } else if (letter <= 127) {
            mode = Mode.SHIFTB;
        } else if (letter <= 159) {
            mode = Mode.SHIFTA;
        } else if (letter <= 223) {
            mode = Mode.AORB;
        } else {
            mode = Mode.SHIFTB;
        }
        if (suppressModeC && mode == Mode.ABORC) {
            mode = Mode.AORB;
        }
        return mode;
    }

    private int length(int letter, Mode mode) {
        if (letter == FNC1 && mode == Mode.ABORC) {
            /* ISO 15417 Annex E Note 2 */
            /* Logical length used for making subset switching decisions, not actual length */
            return 2;
        } else {
            return 1;
        }
    }

    /**
     * Implements rules from ISO 15417 Annex E. Returns the updated index point.
     */
    private int reduceSubsetChanges(Mode[] modeType, int[] modeLength, int indexPoint) {

        int totalLength = 0;
        int i, length;
        Mode current, last, next;

        for (i = 0; i < indexPoint; i++) {
            current = modeType[i];
            length = modeLength[i];
            if (i != 0) {
                last = modeType[i - 1];
            } else {
                last = Mode.NULL;
            }
            if (i != indexPoint - 1) {
                next = modeType[i + 1];
            } else {
                next = Mode.NULL;
            }
            /* ISO 15417 Annex E Note 2 */
            /* Calculate difference between logical length and actual length in this block */
            int extraLength = 0;
            for (int j = 0; j < length - extraLength; j++) {
                if (length(inputData[totalLength + j], current) == 2) {
                    extraLength++;
                }
            }
            if (i == 0) { /* first block */
                if ((indexPoint == 1) && ((length == 2) && (current == Mode.ABORC))) { /* Rule 1a */
                    modeType[i] = Mode.LATCHC;
                    current = Mode.LATCHC;
                }
                if (current == Mode.ABORC) {
                    if (length >= 4) { /* Rule 1b */
                        modeType[i] = Mode.LATCHC;
                        current = Mode.LATCHC;
                    } else {
                        modeType[i] = Mode.AORB;
                        current = Mode.AORB;
                    }
                }
                if (current == Mode.SHIFTA) { /* Rule 1c */
                    modeType[i] = Mode.LATCHA;
                    current = Mode.LATCHA;
                }
                if ((current == Mode.AORB) && (next == Mode.SHIFTA)) { /* Rule 1c */
                    modeType[i] = Mode.LATCHA;
                    current = Mode.LATCHA;
                }
                if (current == Mode.AORB) { /* Rule 1d */
                    modeType[i] = Mode.LATCHB;
                    current = Mode.LATCHB;
                }
            } else {
                if ((current == Mode.ABORC) && (length >= 4)) { /* Rule 3 */
                    modeType[i] = Mode.LATCHC;
                    current = Mode.LATCHC;
                }
                if (current == Mode.ABORC) {
                    modeType[i] = Mode.AORB;
                    current = Mode.AORB;
                }
                if ((current == Mode.AORB) && (last == Mode.LATCHA)) {
                    modeType[i] = Mode.LATCHA;
                    current = Mode.LATCHA;
                }
                if ((current == Mode.AORB) && (last == Mode.LATCHB)) {
                    modeType[i] = Mode.LATCHB;
                    current = Mode.LATCHB;
                }
                if ((current == Mode.AORB) && (next == Mode.SHIFTA)) {
                    modeType[i] = Mode.LATCHA;
                    current = Mode.LATCHA;
                }
                if ((current == Mode.AORB) && (next == Mode.SHIFTB)) {
                    modeType[i] = Mode.LATCHB;
                    current = Mode.LATCHB;
                }
                if (current == Mode.AORB) {
                    modeType[i] = Mode.LATCHB;
                    current = Mode.LATCHB;
                }
                if ((current == Mode.SHIFTA) && (length > 1)) { /* Rule 4 */
                    modeType[i] = Mode.LATCHA;
                    current = Mode.LATCHA;
                }
                if ((current == Mode.SHIFTB) && (length > 1)) { /* Rule 5 */
                    modeType[i] = Mode.LATCHB;
                    current = Mode.LATCHB;
                }
                if ((current == Mode.SHIFTA) && (last == Mode.LATCHA)) {
                    modeType[i] = Mode.LATCHA;
                    current = Mode.LATCHA;
                }
                if ((current == Mode.SHIFTB) && (last == Mode.LATCHB)) {
                    modeType[i] = Mode.LATCHB;
                    current = Mode.LATCHB;
                }
                if ((current == Mode.SHIFTA) && (next == Mode.AORB)) {
                    modeType[i] = Mode.LATCHA;
                    current = Mode.LATCHA;
                }
                if ((current == Mode.SHIFTB) && (next == Mode.AORB)) {
                    modeType[i] = Mode.LATCHB;
                    current = Mode.LATCHB;
                }
                if ((current == Mode.SHIFTA) && (last == Mode.LATCHC)) {
                    modeType[i] = Mode.LATCHA;
                    current = Mode.LATCHA;
                }
                if ((current == Mode.SHIFTB) && (last == Mode.LATCHC)) {
                    modeType[i] = Mode.LATCHB;
                    current = Mode.LATCHB;
                }
            } /* Rule 2 is implemented elsewhere, Rule 6 is implied */

            /* ISO 15417 Annex E Note 2 */
            /* Convert logical length back to actual length for this block, now that we've decided on a subset */
            modeLength[i] -= extraLength;
            totalLength += modeLength[i];
        }

        return combineSubsetBlocks(modeType, modeLength, indexPoint);
    }

    /**
     * Modifies the specified mode and length arrays to combine adjacent modes of the same type, returning the updated index point.
     */
    private int combineSubsetBlocks(Mode[] modeType, int[] modeLength, int indexPoint) {
        /* bring together same type blocks */
        if (indexPoint > 1) {
            for (int i = 1; i < indexPoint; i++) {
                if (modeType[i - 1] == modeType[i]) {
                    /* bring together */
                    modeLength[i - 1] = modeLength[i - 1] + modeLength[i];
                    /* decrease the list */
                    for (int j = i + 1; j < indexPoint; j++) {
                        modeLength[j - 1] = modeLength[j];
                        modeType[j - 1] = modeType[j];
                    }
                    indexPoint--;
                    i--;
                }
            }
        }
        return indexPoint;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected int[] getCodewords() {
        return getPatternAsCodewords(6);
    }
}
