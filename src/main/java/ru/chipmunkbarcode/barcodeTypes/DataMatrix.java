package ru.chipmunkbarcode.barcodeTypes;

import ru.chipmunkbarcode.exceptions.BarcodeException;

import java.util.Arrays;

import static ru.chipmunkbarcode.util.Arrays.positionOf;

/**
 * <p>Implements Data Matrix ECC 200 bar code symbology According to ISO/IEC 16022:2006.
 *
 * <p>Data Matrix is a 2D matrix symbology capable of encoding characters in the
 * ISO/IEC 8859-1 (Latin-1) character set.
 */
public class DataMatrix extends Symbol {

    public enum ForceMode {
        NONE, SQUARE, RECTANGULAR
    }

    private enum Mode {
        NULL, DM_ASCII, DM_C40, DM_TEXT, DM_X12, DM_EDIFACT, DM_BASE256
    }

    private static final int[] C40_SHIFT = {
            1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
            1, 1, 1, 1, 1, 1, 1, 1, 0, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2, 2, 2, 2, 2, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2, 2, 2,
            3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3,
            3, 3, 3, 3, 3, 3, 3, 3
    };

    private static final int[] C40_VALUE = {
            0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19,
            20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 3, 0, 1, 2, 3, 4, 5, 6,
            7, 8, 9, 10, 11, 12, 13, 14, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 15, 16,
            17, 18, 19, 20, 21, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26,
            27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 22, 23, 24, 25, 26,
            0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19,
            20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31
    };

    private static final int[] TEXT_SHIFT = {
            1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
            1, 1, 1, 1, 1, 1, 1, 1, 0, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2, 2, 2, 2, 2, 3, 3, 3, 3, 3, 3, 3,
            3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 2, 2, 2, 2, 2,
            3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 3, 3, 3, 3, 3
    };

    private static final int[] TEXT_VALUE = {
            0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19,
            20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 3, 0, 1, 2, 3, 4, 5, 6,
            7, 8, 9, 10, 11, 12, 13, 14, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 15, 16,
            17, 18, 19, 20, 21, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15,
            16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 22, 23, 24, 25, 26, 0, 14,
            15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32,
            33, 34, 35, 36, 37, 38, 39, 27, 28, 29, 30, 31
    };

    private static final int[] INT_SYMBOL = {
            0, 1, 3, 5, 7, 8, 10, 12, 13, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24,
            25, 26, 27, 28, 29, 2, 4, 6, 9, 11, 14
    };

    private static final int[] MATRIX_H = {
            10, 12, 8, 14, 8, 16, 12, 18, 20, 12, 22, 16, 24, 26, 16, 32, 36, 40,
            44, 48, 52, 64, 72, 80, 88, 96, 104, 120, 132, 144
    };

    private static final int[] MATRIX_W = {
            10, 12, 18, 14, 32, 16, 26, 18, 20, 36, 22, 36, 24, 26, 48, 32, 36, 40,
            44, 48, 52, 64, 72, 80, 88, 96, 104, 120, 132, 144
    };

    private static final int[] MATRIX_FH = {
            10, 12, 8, 14, 8, 16, 12, 18, 20, 12, 22, 16, 24, 26, 16, 16, 18, 20,
            22, 24, 26, 16, 18, 20, 22, 24, 26, 20, 22, 24
    };

    private static final int[] MATRIX_FW = {
            10, 12, 18, 14, 16, 16, 26, 18, 20, 18, 22, 18, 24, 26, 24, 16, 18, 20,
            22, 24, 26, 16, 18, 20, 22, 24, 26, 20, 22, 24
    };

    private static final int[] MATRIX_BYTES = {
            3, 5, 5, 8, 10, 12, 16, 18, 22, 22, 30, 32, 36, 44, 49, 62, 86, 114,
            144, 174, 204, 280, 368, 456, 576, 696, 816, 1050, 1304, 1558
    };

    private static final int[] MATRIX_DATA_BLOCK = {
            3, 5, 5, 8, 10, 12, 16, 18, 22, 22, 30, 32, 36, 44, 49, 62, 86, 114,
            144, 174, 102, 140, 92, 114, 144, 174, 136, 175, 163, 156
    };

    private static final int[] MATRIX_RS_BLOCK = {
            5, 7, 7, 10, 11, 12, 14, 14, 18, 18, 20, 24, 24, 28, 28, 36, 42, 48, 56,
            68, 42, 56, 36, 48, 56, 68, 56, 68, 62, 62
    };

    private static final int DM_SIZES_COUNT = MATRIX_H.length;

    // user-specified values and settings

    private ForceMode forceMode = ForceMode.NONE;
    private int preferredSize;
    private int structuredAppendFileId = 1;
    private int structuredAppendPosition = 1;
    private int structuredAppendTotal = 1;

    // internal state calculated when setContent() is called

    private int actualSize = -1;
    private final int[] target = new int[2200];
    private final int[] binary = new int[2200];
    private int binaryLength;
    private Mode lastMode;
    private int[] places;
    private int processP;
    private final int[] processBuffer = new int[8];
    private int codewordCount;

    /**
     * Forces the symbol to be either square or rectangular (non-square).
     *
     * @param forceMode the force mode to use
     */
    public void setForceMode(ForceMode forceMode) {
        this.forceMode = forceMode;
    }

    /**
     * Returns the force mode used by this symbol.
     *
     * @return the force mode used by this symbol
     */
    public ForceMode getForceMode() {
        return forceMode;
    }

    /**
     * Sets the preferred symbol size according to the values in the following
     * table. Values may be ignored if the data is too big to fit in the
     * specified symbol, or if {@link #setForceMode(ForceMode)} has been invoked.
     *
     * <table>
     * <tbody>
     * <tr><th>Input</th><th>Symbol Size</th><th>Input</th><th>Symbol Size</th></tr>
     * <tr><td>1    </td><td>10 x 10    </td><td>16</td><td>64 x 64       </td></tr>
     * <tr><td>2    </td><td>12 x 12    </td><td>17</td><td>72 x 72       </td></tr>
     * <tr><td>3    </td><td>14 x 14    </td><td>18</td><td>80 x 80       </td></tr>
     * <tr><td>4    </td><td>16 x 16    </td><td>19</td><td>88 x 88       </td></tr>
     * <tr><td>5    </td><td>18 x 18    </td><td>20</td><td>96 x 96       </td></tr>
     * <tr><td>6    </td><td>20 x 20    </td><td>21</td><td>104 x 104     </td></tr>
     * <tr><td>7    </td><td>22 x 22    </td><td>22</td><td>120 x 120     </td></tr>
     * <tr><td>8    </td><td>24 x 24    </td><td>23</td><td>132 x 132     </td></tr>
     * <tr><td>9    </td><td>26 x 26    </td><td>24</td><td>144 x 144     </td></tr>
     * <tr><td>10   </td><td>32 x 32    </td><td>25</td><td>8 x 18        </td></tr>
     * <tr><td>11   </td><td>36 x 36    </td><td>26</td><td>8 x 32        </td></tr>
     * <tr><td>12   </td><td>40 x 40    </td><td>27</td><td>12 x 26       </td></tr>
     * <tr><td>13   </td><td>44 x 44    </td><td>28</td><td>12 x 36       </td></tr>
     * <tr><td>14   </td><td>48 x 48    </td><td>29</td><td>16 x 36       </td></tr>
     * <tr><td>15   </td><td>52 x 52    </td><td>30</td><td>16 x 48       </td></tr>
     * </tbody>
     * </table>
     *
     * @param size the symbol size to use (1 - 30 inclusive)
     */
    public void setPreferredSize(int size) {
        preferredSize = size;
    }

    /**
     * Returns the preferred symbol size.
     *
     * @return the preferred symbol size
     * @see #setPreferredSize(int)
     */
    public int getPreferredSize() {
        return preferredSize;
    }

    /**
     * Returns the actual symbol size used. Available after the symbol is encoded.
     *
     * @return the actual symbol size used
     */
    public int getActualSize() {
        if (actualSize != -1) {
            return actualSize;
        } else {
            throw new IllegalStateException("Actual size not calculated until symbol is encoded.");
        }
    }

    /**
     * Returns the actual width (columns) used for the symbol. Available after the symbol is encoded.
     *
     * @return the actual width (columns) used for the symbol
     */
    public int getActualWidth() {
        int index1 = getActualSize() - 1;
        int index2 = INT_SYMBOL[index1];
        return MATRIX_W[index2];
    }

    /**
     * Returns the actual height (rows) used for the symbol. Available after the symbol is encoded.
     *
     * @return the actual height (rows) used for the symbol
     */
    public int getActualHeight() {
        int index1 = getActualSize() - 1;
        int index2 = INT_SYMBOL[index1];
        return MATRIX_H[index2];
    }

    /**
     * If this Data Matrix symbol is part of a series of Data Matrix symbols appended in a structured
     * format, this method sets the position of this symbol in the series. Valid values are 1 through
     * 16 inclusive.
     *
     * @param position the position of this Data Matrix symbol in the structured append series
     */
    public void setStructuredAppendPosition(int position) {
        if (position < 1 || position > 16) {
            throw new IllegalArgumentException("Invalid Data Matrix structured append position: " + position);
        }
        this.structuredAppendPosition = position;
    }

    /**
     * Returns the position of this Data Matrix symbol in a series of symbols using structured append.
     * If this symbol is not part of such a series, this method will return <code>1</code>.
     *
     * @return the position of this Data Matrix symbol in a series of symbols using structured append
     */
    public int getStructuredAppendPosition() {
        return structuredAppendPosition;
    }

    /**
     * If this Data Matrix symbol is part of a series of Data Matrix symbols appended in a structured
     * format, this method sets the total number of symbols in the series. Valid values are
     * 1 through 16 inclusive. A value of 1 indicates that this symbol is not part of a structured
     * append series.
     *
     * @param total the total number of Data Matrix symbols in the structured append series
     */
    public void setStructuredAppendTotal(int total) {
        if (total < 1 || total > 16) {
            throw new IllegalArgumentException("Invalid Data Matrix structured append total: " + total);
        }
        this.structuredAppendTotal = total;
    }

    /**
     * Returns the size of the series of Data Matrix symbols using structured append that this symbol
     * is part of. If this symbol is not part of a structured append series, this method will return
     * <code>1</code>.
     *
     * @return size of the series that this symbol is part of
     */
    public int getStructuredAppendTotal() {
        return structuredAppendTotal;
    }

    /**
     * If this Data Matrix symbol is part of a series of Data Matrix symbols appended in a structured format,
     * this method sets the unique file ID for the series. Valid values are 1 through 64,516 inclusive.
     *
     * @param fileId the unique file ID for the series that this symbol is part of
     */
    public void setStructuredAppendFileId(int fileId) {
        if (fileId < 1 || fileId > 64_516) {
            throw new IllegalArgumentException("Invalid Data Matrix structured append file ID: " + fileId);
        }
        this.structuredAppendFileId = fileId;
    }

    /**
     * Returns the unique file ID of the series of Data Matrix symbols using structured append that this
     * symbol is part of. If this symbol is not part of a structured append series, this method will return
     * <code>1</code>.
     *
     * @return the unique file ID for the series that this symbol is part of
     */
    public int getStructuredAppendFileId() {
        return structuredAppendFileId;
    }

    @Override
    protected boolean gs1Supported() {
        return true;
    }

    @Override
    protected void encode() {

        int i, binLen, skew = 0;
        int symbolSize, optionSize, calcSize;
        int taillength;
        int H, W, FH, FW, datablock, bytes, rsblock;
        int x, y, NC, NR, v;
        int[] grid;
        StringBuilder bin = new StringBuilder();

        eciProcess(); // Get ECI mode

        binLen = generateCodewords();

        if (preferredSize >= 1 && preferredSize <= DM_SIZES_COUNT) {
            optionSize = INT_SYMBOL[preferredSize - 1];
        } else {
            optionSize = -1;
        }

        calcSize = DM_SIZES_COUNT - 1;
        for (i = DM_SIZES_COUNT - 1; i > -1; i--) {
            if (MATRIX_BYTES[i] >= (binLen + processP)) {
                calcSize = i;
            }
        }

        if (optionSize == -1) {
            // We are in automatic size mode as the exact symbol size was not given
            // Now check the detailed search options square only or rectangular only
            if (forceMode == ForceMode.SQUARE) {
                /* Skip rectangular symbols in square only mode */
                while (calcSize < DM_SIZES_COUNT && MATRIX_H[calcSize] != MATRIX_W[calcSize]) {
                    calcSize++;
                }
            } else if (forceMode == ForceMode.RECTANGULAR) {
                /* Skip square symbols in rectangular only mode */
                while (calcSize < DM_SIZES_COUNT && MATRIX_H[calcSize] == MATRIX_W[calcSize]) {
                    calcSize++;
                }
            }
            if (calcSize >= DM_SIZES_COUNT) {
                throw new BarcodeException("Data too long to fit in symbol");
            }
            symbolSize = calcSize;
        } else {
            // The symbol size was specified by the user
            // Thus check if the data fits into this symbol size and use this size
            if (calcSize > optionSize) {
                throw new BarcodeException("Input too long for selected symbol size");
            }
            symbolSize = optionSize;
        }

        // Now we know the symbol size we can handle the remaining data in the process buffer.
        int symbolsLeft = MATRIX_BYTES[symbolSize] - binLen;
        binLen = encodeRemainder(symbolsLeft, binLen);

        if (binLen > MATRIX_BYTES[symbolSize]) {
            throw new BarcodeException("Data too long to fit in symbol");
        }

        H = MATRIX_H[symbolSize];
        W = MATRIX_W[symbolSize];
        FH = MATRIX_FH[symbolSize];
        FW = MATRIX_FW[symbolSize];
        bytes = MATRIX_BYTES[symbolSize];
        datablock = MATRIX_DATA_BLOCK[symbolSize];
        rsblock = MATRIX_RS_BLOCK[symbolSize];

        codewordCount = datablock + rsblock; // data codewords + error correction codewords

        taillength = bytes - binLen;

        if (taillength != 0) {
            addPadBits(binLen, taillength);
        }

        // ecc code
        if (symbolSize == 29) {
            skew = 1;
        }
        calculateErrorCorrection(bytes, datablock, rsblock, skew);
        NC = W - 2 * (W / FW);
        NR = H - 2 * (H / FH);
        places = new int[NC * NR];
        placeData(NR, NC);
        grid = new int[W * H];
        for (i = 0; i < (W * H); i++) {
            grid[i] = 0;
        }
        for (y = 0; y < H; y += FH) {
            for (x = 0; x < W; x++) {
                grid[y * W + x] = 1;
            }
            for (x = 0; x < W; x += 2) {
                grid[(y + FH - 1) * W + x] = 1;
            }
        }
        for (x = 0; x < W; x += FW) {
            for (y = 0; y < H; y++) {
                grid[y * W + x] = 1;
            }
            for (y = 0; y < H; y += 2) {
                grid[y * W + x + FW - 1] = 1;
            }
        }
        for (y = 0; y < NR; y++) {
            for (x = 0; x < NC; x++) {
                v = places[(NR - y - 1) * NC + x];
                if (v == 1 || (v > 7 && (target[(v >> 3) - 1] & (1 << (v & 7))) != 0)) {
                    grid[(1 + y + 2 * (y / (FH - 2))) * W + 1 + x + 2 * (x / (FW - 2))] = 1;
                }
            }
        }

        actualSize = positionOf(symbolSize, INT_SYMBOL) + 1;
        readable = "";
        pattern = new String[H];
        row_count = H;
        rowHeight = new int[H];
        for (y = H - 1; y >= 0; y--) {
            bin.setLength(0);
            for (x = 0; x < W; x++) {
                if (grid[W * y + x] == 1) {
                    bin.append('1');
                } else {
                    bin.append('0');
                }
            }
            pattern[(H - y) - 1] = bin2pat(bin);
            rowHeight[(H - y) - 1] = moduleWidth;
        }

        infoLine("Grid Size: " + W + " X " + H);
        infoLine("Data Codewords: " + datablock);
        infoLine("ECC Codewords: " + rsblock);
    }

    @Override
    protected int[] getCodewords() {
        return Arrays.copyOf(target, codewordCount);
    }

    private int generateCodewords() {
        /* Encodes data using ASCII, C40, Text, X12, EDIFACT or Base 256 modes as appropriate */
        /* Supports encoding FNC1 in supporting systems */
        /* Supports ECI encoding for whole message only, not inline switching */

        info("Encoding: ");
        int sp, tp, i;
        Mode currentMode, nextMode;
        int inputLength = inputData.length;

        sp = 0;
        tp = 0;
        processP = 0;

        for (i = 0; i < 8; i++) {
            processBuffer[i] = 0;
        }
        binaryLength = 0;

        /* step (a) */
        currentMode = Mode.DM_ASCII;
        nextMode = Mode.DM_ASCII;

        if (structuredAppendTotal != 1) {

            /* FNC2 */
            target[tp] = 233;
            tp++;
            binary[binaryLength] = ' ';
            binaryLength++;
            info("FNC2 ");

            /* symbol sequence indicator (position + total) */
            int ssi = ((structuredAppendPosition - 1) << 4) | (17 - structuredAppendTotal);
            target[tp] = ssi;
            tp++;
            binary[binaryLength] = ' ';
            binaryLength++;
            infoSpace(ssi);

            /* file identification codeword 1 (valid values 1 - 254) */
            int id1 = 1 + ((structuredAppendFileId - 1) / 254);
            target[tp] = id1;
            tp++;
            binary[binaryLength] = ' ';
            binaryLength++;
            infoSpace(id1);

            /* file identification codeword 2 (valid values 1 - 254) */
            int id2 = 1 + ((structuredAppendFileId - 1) % 254);
            target[tp] = id2;
            tp++;
            binary[binaryLength] = ' ';
            binaryLength++;
            infoSpace(id2);
        }

        if (inputDataType == DataType.GS1) {
            target[tp] = 232;
            tp++;
            binary[binaryLength] = ' ';
            binaryLength++;
            info("FNC1 ");
        } /* FNC1 */

        if (readerInit) {
            if (inputDataType == DataType.GS1) {
                throw new BarcodeException("Cannot encode in GS1 mode and Reader Initialisation at the same time");
            } else {
                target[tp] = 234; /* FNC3 */
                tp++; /* Reader Programming */
                binary[binaryLength] = ' ';
                binaryLength++;
                info("RP ");
            }
        }

        if (eciMode != 3) {
            target[tp] = 241; // ECI
            tp++;
            binary[binaryLength] = ' ';
            binaryLength++;
            if (eciMode <= 126) {
                target[tp] = eciMode + 1;
                tp++;
                binary[binaryLength] = ' ';
                binaryLength++;
            }
            if ((eciMode >= 127) && (eciMode <= 16382)) {
                target[tp] = ((eciMode - 127) / 254) + 128;
                tp++;
                binary[binaryLength] = ' ';
                binaryLength++;
                target[tp] = ((eciMode - 127) % 254) + 1;
                tp++;
                binary[binaryLength] = ' ';
                binaryLength++;
            }
            if (eciMode >= 16383) {
                target[tp] = ((eciMode - 16383) / 64516) + 192;
                tp++;
                binary[binaryLength] = ' ';
                binaryLength++;
                target[tp] = (((eciMode - 16383) / 254) % 254) + 1;
                tp++;
                binary[binaryLength] = ' ';
                binaryLength++;
                target[tp] = ((eciMode - 16383) % 254) + 1;
                tp++;
                binary[binaryLength] = ' ';
                binaryLength++;
            }
            info("ECI " + eciMode + " ");
        }

        /* Check for Macro05/Macro06 */
        /* "[)>[RS]05[GS]...[RS][EOT]" -> CW 236 */
        /* "[)>[RS]06[GS]...[RS][EOT]" -> CW 237 */
        if (tp == 0 && sp == 0 && inputLength >= 9) {
            if (inputData[0] == '[' && inputData[1] == ')' && inputData[2] == '>'
                    && inputData[3] == '\u001e' && inputData[4] == '0'
                    && (inputData[5] == '5' || inputData[5] == '6')
                    && inputData[6] == '\u001d'
                    && inputData[inputLength - 2] == '\u001e'
                    && inputData[inputLength - 1] == '\u0004') {
                /* Output macro codeword */
                if (inputData[5] == '5') {
                    target[tp] = 236;
                    info("Macro05 ");
                } else {
                    target[tp] = 237;
                    info("Macro06 ");
                }
                tp++;
                binary[binaryLength] = ' ';
                binaryLength++;
                /* Remove macro characters from input string */
                sp = 7;
                inputLength -= 2;
                inputData = Arrays.copyOf(inputData, inputData.length - 2);
            }
        }

        while (sp < inputLength) {
            currentMode = nextMode;
            /* step (b) - ASCII encodation */
            if (currentMode == Mode.DM_ASCII) {
                nextMode = Mode.DM_ASCII;
                for (i = 0; i < 8; i++) {
                    processBuffer[i] = 0;
                }
                if (isTwoDigits(sp)) {
                    target[tp] = (10 * Character.getNumericValue(inputData[sp]))
                            + Character.getNumericValue(inputData[sp + 1]) + 130;
                    infoSpace(target[tp] - 130);
                    tp++;
                    binary[binaryLength] = ' ';
                    binaryLength++;
                    sp += 2;
                } else {
                    nextMode = lookAheadTest(sp, currentMode);

                    if (nextMode != Mode.DM_ASCII) {
                        switch (nextMode) {
                            case DM_C40:
                                target[tp] = 230;
                                tp++;
                                binary[binaryLength] = ' ';
                                binaryLength++;
                                info("C40 ");
                                break;
                            case DM_TEXT:
                                target[tp] = 239;
                                tp++;
                                binary[binaryLength] = ' ';
                                binaryLength++;
                                info("TEX ");
                                break;
                            case DM_X12:
                                target[tp] = 238;
                                tp++;
                                binary[binaryLength] = ' ';
                                binaryLength++;
                                info("X12 ");
                                break;
                            case DM_EDIFACT:
                                target[tp] = 240;
                                tp++;
                                binary[binaryLength] = ' ';
                                binaryLength++;
                                info("EDI ");
                                break;
                            case DM_BASE256:
                                target[tp] = 231;
                                tp++;
                                binary[binaryLength] = ' ';
                                binaryLength++;
                                info("BAS ");
                                break;
                        }
                    } else {
                        if (inputData[sp] > 127) {
                            target[tp] = 235; /* FNC4 */

                            info("FNC4 ");
                            tp++;
                            target[tp] = (inputData[sp] - 128) + 1;
                            infoSpace(target[tp] - 1);
                            tp++;
                            binary[binaryLength] = ' ';
                            binaryLength++;
                            binary[binaryLength] = ' ';
                            binaryLength++;
                        } else {
                            if (inputData[sp] == FNC1) {
                                target[tp] = 232; /* FNC1 */
                                info("FNC1 ");
                            } else {
                                target[tp] = inputData[sp] + 1;
                                infoSpace(target[tp] - 1);
                            }
                            tp++;
                            binary[binaryLength] = ' ';
                            binaryLength++;
                        }
                        sp++;
                    }
                }
            }

            /* step (c) C40 encodation */
            if (currentMode == Mode.DM_C40) {
                int shiftSet, value;

                nextMode = Mode.DM_C40;
                if (processP == 0) {
                    nextMode = lookAheadTest(sp, currentMode);
                }

                if (nextMode != Mode.DM_C40) {
                    target[tp] = 254;
                    tp++;
                    binary[binaryLength] = ' ';
                    binaryLength++; /* Unlatch */

                    nextMode = Mode.DM_ASCII;
                    info("ASC ");
                } else {
                    if (inputData[sp] == FNC1) {
                        shiftSet = 2;
                        value = 27; /* FNC1 */
                    } else if (inputData[sp] > 127) {
                        processBuffer[processP] = 1;
                        processP++;
                        processBuffer[processP] = 30;
                        processP++; /* Upper Shift */

                        shiftSet = C40_SHIFT[inputData[sp] - 128];
                        value = C40_VALUE[inputData[sp] - 128];
                    } else {
                        shiftSet = C40_SHIFT[inputData[sp]];
                        value = C40_VALUE[inputData[sp]];
                    }

                    if (shiftSet != 0) {
                        processBuffer[processP] = shiftSet - 1;
                        processP++;
                    }
                    processBuffer[processP] = value;
                    processP++;

                    while (processP >= 3) {
                        int iv;

                        iv = (1600 * processBuffer[0]) + (40 * processBuffer[1])
                                + (processBuffer[2]) + 1;
                        target[tp] = iv / 256;
                        tp++;
                        target[tp] = iv % 256;
                        tp++;
                        binary[binaryLength] = ' ';
                        binaryLength++;
                        binary[binaryLength] = ' ';
                        binaryLength++;
                        info("(" + processBuffer[0] + " " + processBuffer[1] + " " + processBuffer[2] + ") ");

                        processBuffer[0] = processBuffer[3];
                        processBuffer[1] = processBuffer[4];
                        processBuffer[2] = processBuffer[5];
                        processBuffer[3] = 0;
                        processBuffer[4] = 0;
                        processBuffer[5] = 0;
                        processP -= 3;
                    }
                    sp++;
                }
            }

            /* step (d) Text encodation */
            if (currentMode == Mode.DM_TEXT) {
                int shiftSet, value;

                nextMode = Mode.DM_TEXT;
                if (processP == 0) {
                    nextMode = lookAheadTest(sp, currentMode);
                }

                if (nextMode != Mode.DM_TEXT) {
                    target[tp] = 254;
                    tp++;
                    binary[binaryLength] = ' ';
                    binaryLength++; /* Unlatch */

                    nextMode = Mode.DM_ASCII;
                    info("ASC ");
                } else {
                    if (inputData[sp] == FNC1) {
                        shiftSet = 2;
                        value = 27; /* FNC1 */
                    } else if (inputData[sp] > 127) {
                        processBuffer[processP] = 1;
                        processP++;
                        processBuffer[processP] = 30;
                        processP++; /* Upper Shift */

                        shiftSet = TEXT_SHIFT[inputData[sp] - 128];
                        value = TEXT_VALUE[inputData[sp] - 128];
                    } else {
                        shiftSet = TEXT_SHIFT[inputData[sp]];
                        value = TEXT_VALUE[inputData[sp]];
                    }

                    if (shiftSet != 0) {
                        processBuffer[processP] = shiftSet - 1;
                        processP++;
                    }
                    processBuffer[processP] = value;
                    processP++;

                    while (processP >= 3) {
                        int iv;

                        iv = (1600 * processBuffer[0]) + (40 * processBuffer[1])
                                + (processBuffer[2]) + 1;
                        target[tp] = iv / 256;
                        tp++;
                        target[tp] = iv % 256;
                        tp++;
                        binary[binaryLength] = ' ';
                        binaryLength++;
                        binary[binaryLength] = ' ';
                        binaryLength++;
                        info("(" + processBuffer[0] + " " + processBuffer[1] + " " + processBuffer[2] + ") ");

                        processBuffer[0] = processBuffer[3];
                        processBuffer[1] = processBuffer[4];
                        processBuffer[2] = processBuffer[5];
                        processBuffer[3] = 0;
                        processBuffer[4] = 0;
                        processBuffer[5] = 0;
                        processP -= 3;
                    }
                    sp++;
                }
            }

            /* step (e) X12 encodation */
            if (currentMode == Mode.DM_X12) {
                int value = 0;

                nextMode = Mode.DM_X12;
                if (processP == 0) {
                    nextMode = lookAheadTest(sp, currentMode);
                }

                if (nextMode != Mode.DM_X12) {
                    target[tp] = 254;
                    tp++;
                    binary[binaryLength] = ' ';
                    binaryLength++; /* Unlatch */

                    nextMode = Mode.DM_ASCII;
                    info("ASC ");
                } else {
                    if (inputData[sp] == 13) {
                        value = 0;
                    }
                    if (inputData[sp] == '*') {
                        value = 1;
                    }
                    if (inputData[sp] == '>') {
                        value = 2;
                    }
                    if (inputData[sp] == ' ') {
                        value = 3;
                    }
                    if ((inputData[sp] >= '0') && (inputData[sp] <= '9')) {
                        value = (inputData[sp] - '0') + 4;
                    }
                    if ((inputData[sp] >= 'A') && (inputData[sp] <= 'Z')) {
                        value = (inputData[sp] - 'A') + 14;
                    }

                    processBuffer[processP] = value;
                    processP++;

                    while (processP >= 3) {
                        int iv;

                        iv = (1600 * processBuffer[0]) + (40 * processBuffer[1])
                                + (processBuffer[2]) + 1;
                        target[tp] = iv / 256;
                        tp++;
                        target[tp] = iv % 256;
                        tp++;
                        binary[binaryLength] = ' ';
                        binaryLength++;
                        binary[binaryLength] = ' ';
                        binaryLength++;
                        info("(" + processBuffer[0] + " " + processBuffer[1] + " " + processBuffer[2] + ") ");

                        processBuffer[0] = processBuffer[3];
                        processBuffer[1] = processBuffer[4];
                        processBuffer[2] = processBuffer[5];
                        processBuffer[3] = 0;
                        processBuffer[4] = 0;
                        processBuffer[5] = 0;
                        processP -= 3;
                    }
                    sp++;
                }
            }

            /* step (f) EDIFACT encodation */
            if (currentMode == Mode.DM_EDIFACT) {
                int value = 0;

                nextMode = Mode.DM_EDIFACT;
                if (processP == 3) {
                    nextMode = lookAheadTest(sp, currentMode);
                }

                if (nextMode != Mode.DM_EDIFACT) {
                    processBuffer[processP] = 31;
                    processP++;
                    nextMode = Mode.DM_ASCII;
                } else {
                    if ((inputData[sp] >= '@') && (inputData[sp] <= '^')) {
                        value = inputData[sp] - '@';
                    }
                    if ((inputData[sp] >= ' ') && (inputData[sp] <= '?')) {
                        value = inputData[sp];
                    }

                    processBuffer[processP] = value;
                    processP++;
                    sp++;
                }

                while (processP >= 4) {
                    target[tp] = (processBuffer[0] << 2)
                            + ((processBuffer[1] & 0x30) >> 4);
                    tp++;
                    target[tp] = ((processBuffer[1] & 0x0f) << 4)
                            + ((processBuffer[2] & 0x3c) >> 2);
                    tp++;
                    target[tp] = ((processBuffer[2] & 0x03) << 6)
                            + processBuffer[3];
                    tp++;
                    binary[binaryLength] = ' ';
                    binaryLength++;
                    binary[binaryLength] = ' ';
                    binaryLength++;
                    binary[binaryLength] = ' ';
                    binaryLength++;
                    info("(" + processBuffer[0] + " " + processBuffer[1] + " " + processBuffer[2] + ") ");

                    processBuffer[0] = processBuffer[4];
                    processBuffer[1] = processBuffer[5];
                    processBuffer[2] = processBuffer[6];
                    processBuffer[3] = processBuffer[7];
                    processBuffer[4] = 0;
                    processBuffer[5] = 0;
                    processBuffer[6] = 0;
                    processBuffer[7] = 0;
                    processP -= 4;
                }
            }

            /* step (g) Base 256 encodation */
            if (currentMode == Mode.DM_BASE256) {
                nextMode = lookAheadTest(sp, currentMode);

                if (nextMode == Mode.DM_BASE256) {
                    target[tp] = inputData[sp];
                    infoSpace(target[tp]);
                    tp++;
                    sp++;
                    binary[binaryLength] = 'b';
                    binaryLength++;
                } else {
                    nextMode = Mode.DM_ASCII;
                    info("ASC ");
                }
            }

            if (tp > 1558) {
                throw new BarcodeException("Data too long to fit in symbol");
            }

        } /* while */

        /* Add length and randomising algorithm to b256 */
        i = 0;
        while (i < tp) {
            if (binary[i] == 'b') {
                if ((i == 0) || (binary[i - 1] != 'b')) {
                    /* start of binary data */
                    int binaryCount; /* length of b256 data */

                    for (binaryCount = 0; binaryCount + i < tp && binary[binaryCount + i] == 'b'; binaryCount++) ;

                    if (binaryCount <= 249) {
                        insertAt(i, 'b');
                        insertValueAt(i, tp, (char) binaryCount);
                        tp++;
                    } else {
                        insertAt(i, 'b');
                        insertAt(i + 1, 'b');
                        insertValueAt(i, tp, (char) ((binaryCount / 250) + 249));
                        tp++;
                        insertValueAt(i + 1, tp, (char) (binaryCount % 250));
                        tp++;
                    }
                }
            }
            i++;
        }

        for (i = 0; i < tp; i++) {
            if (binary[i] == 'b') {
                int prn, temp;

                prn = ((149 * (i + 1)) % 255) + 1;
                temp = target[i] + prn;
                if (temp <= 255) {
                    target[i] = temp;
                } else {
                    target[i] = temp - 256;
                }
            }
        }

        infoLine();
        info("Codewords: ");
        for (i = 0; i < tp; i++) {
            infoSpace(target[i]);
        }
        infoLine();

        lastMode = currentMode;
        return tp;
    }

    private int encodeRemainder(int symbolsLeft, int targetLength) {

        int inputlen = inputData.length;

        switch (lastMode) {
            case DM_C40:
            case DM_TEXT:
                if (processP == 1) // 1 data character left to encode.
                {
                    if (symbolsLeft > 1) {
                        target[targetLength] = 254;
                        targetLength++; // Unlatch and encode remaining data in ascii.
                    }
                    target[targetLength] = inputData[inputlen - 1] + 1;
                    targetLength++;
                } else if (processP == 2) // 2 data characters left to encode.
                {
                    // Pad with shift 1 value (0) and encode as double.
                    int intValue = (1600 * processBuffer[0]) + (40 * processBuffer[1]) + 1; // ie (0 + 1).
                    target[targetLength] = (intValue / 256);
                    targetLength++;
                    target[targetLength] = (intValue % 256);
                    targetLength++;
                    if (symbolsLeft > 2) {
                        target[targetLength] = 254; // Unlatch
                        targetLength++;
                    }
                } else {
                    if (symbolsLeft > 0) {
                        target[targetLength] = 254; // Unlatch
                        targetLength++;
                    }
                }
                break;

            case DM_X12:
                if ((symbolsLeft == processP) && (processP == 1)) {
                    // Unlatch not required!
                    target[targetLength] = inputData[inputlen - 1] + 1;
                    targetLength++;
                } else {
                    target[targetLength] = (254);
                    targetLength++; // Unlatch.

                    if (processP == 1) {
                        target[targetLength] = inputData[inputlen - 1] + 1;
                        targetLength++;
                    }

                    if (processP == 2) {
                        target[targetLength] = inputData[inputlen - 2] + 1;
                        targetLength++;
                        target[targetLength] = inputData[inputlen - 1] + 1;
                        targetLength++;
                    }
                }
                break;

            case DM_EDIFACT:
                if (symbolsLeft <= 2) // Unlatch not required!
                {
                    if (processP == 1) {
                        target[targetLength] = inputData[inputlen - 1] + 1;
                        targetLength++;
                    }

                    if (processP == 2) {
                        target[targetLength] = inputData[inputlen - 2] + 1;
                        targetLength++;
                        target[targetLength] = inputData[inputlen - 1] + 1;
                        targetLength++;
                    }
                } else {
                    // Append edifact unlatch value (31) and empty buffer
                    if (processP == 0) {
                        target[targetLength] = (31 << 2);
                        targetLength++;
                    }

                    if (processP == 1) {
                        target[targetLength] = ((processBuffer[0] << 2) + ((31 & 0x30) >> 4));
                        targetLength++;
                        target[targetLength] = ((31 & 0x0f) << 4);
                        targetLength++;
                    }

                    if (processP == 2) {
                        target[targetLength] = ((processBuffer[0] << 2) + ((processBuffer[1] & 0x30) >> 4));
                        targetLength++;
                        target[targetLength] = (((processBuffer[1] & 0x0f) << 4) + ((31 & 0x3c) >> 2));
                        targetLength++;
                        target[targetLength] = (((31 & 0x03) << 6));
                        targetLength++;
                    }

                    if (processP == 3) {
                        target[targetLength] = ((processBuffer[0] << 2) + ((processBuffer[1] & 0x30) >> 4));
                        targetLength++;
                        target[targetLength] = (((processBuffer[1] & 0x0f) << 4) + ((processBuffer[2] & 0x3c) >> 2));
                        targetLength++;
                        target[targetLength] = (((processBuffer[2] & 0x03) << 6) + 31);
                        targetLength++;
                    }
                }
                break;
        }

        return targetLength;
    }

    private boolean isTwoDigits(int pos) {
        return pos + 1 < inputData.length &&
                Character.isDigit((char) inputData[pos]) &&
                Character.isDigit((char) inputData[pos + 1]);
    }

    private Mode lookAheadTest(int position, Mode currentMode) {
        /* 'look ahead test' from Annex P */
        double asciiCount, c40Count, textCount, x12Count, edfCount, b256Count, bestCount;
        int sp;
        int sourcelen = inputData.length;
        Mode bestScheme = Mode.NULL;
        double stiction = (1.0F / 24.0F); // smallest change to act on, to get around floating point inaccuracies

        /* step (j) */
        if (currentMode == Mode.DM_ASCII) {
            asciiCount = 0.0;
            c40Count = 1.0;
            textCount = 1.0;
            x12Count = 1.0;
            edfCount = 1.0;
            b256Count = 1.25;
        } else {
            asciiCount = 1.0;
            c40Count = 2.0;
            textCount = 2.0;
            x12Count = 2.0;
            edfCount = 2.0;
            b256Count = 2.25;
        }

        switch (currentMode) {
            case DM_C40: // (j)(2)
                c40Count = 0.0;
                break;
            case DM_TEXT: // (j)(3)
                textCount = 0.0;
                break;
            case DM_X12: // (j)(4)
                x12Count = 0.0;
                break;
            case DM_EDIFACT: // (j)(5)
                edfCount = 0.0;
                break;
            case DM_BASE256: // (j)(6)
                b256Count = 0.0;
                break;
        }

        sp = position;

        do {
            if (sp == sourcelen) {
                /* At the end of data ... step (k) */
                asciiCount = Math.ceil(asciiCount);
                b256Count = Math.ceil(b256Count);
                edfCount = Math.ceil(edfCount);
                textCount = Math.ceil(textCount);
                x12Count = Math.ceil(x12Count);
                c40Count = Math.ceil(c40Count);

                bestCount = c40Count;
                bestScheme = Mode.DM_C40; // (k)(7)

                if (x12Count < (bestCount - stiction)) {
                    bestCount = x12Count;
                    bestScheme = Mode.DM_X12; // (k)(6)
                }

                if (textCount < (bestCount - stiction)) {
                    bestCount = textCount;
                    bestScheme = Mode.DM_TEXT; // (k)(5)
                }

                if (edfCount < (bestCount - stiction)) {
                    bestCount = edfCount;
                    bestScheme = Mode.DM_EDIFACT; // (k)(4)
                }

                if (b256Count < (bestCount - stiction)) {
                    bestCount = b256Count;
                    bestScheme = Mode.DM_BASE256; // (k)(3)
                }

                if (asciiCount <= (bestCount + stiction)) {
                    bestScheme = Mode.DM_ASCII; // (k)(2)
                }
            } else {

                /* ascii ... step (l) */
                if ((inputData[sp] >= '0') && (inputData[sp] <= '9')) {
                    asciiCount += 0.5; // (l)(1)
                } else {
                    if (inputData[sp] > 127) {
                        asciiCount = Math.ceil(asciiCount) + 2.0; // (l)(2)
                    } else {
                        asciiCount = Math.ceil(asciiCount) + 1.0; // (l)(3)
                    }
                }

                /* c40 ... step (m) */
                if ((inputData[sp] == ' ') ||
                        (((inputData[sp] >= '0') && (inputData[sp] <= '9')) ||
                                ((inputData[sp] >= 'A') && (inputData[sp] <= 'Z')))) {
                    c40Count += (2.0 / 3.0); // (m)(1)
                } else {
                    if (inputData[sp] > 127) {
                        c40Count += (8.0 / 3.0); // (m)(2)
                    } else {
                        c40Count += (4.0 / 3.0); // (m)(3)
                    }
                }

                /* text ... step (n) */
                if ((inputData[sp] == ' ') ||
                        (((inputData[sp] >= '0') && (inputData[sp] <= '9')) ||
                                ((inputData[sp] >= 'a') && (inputData[sp] <= 'z')))) {
                    textCount += (2.0 / 3.0); // (n)(1)
                } else {
                    if (inputData[sp] > 127) {
                        textCount += (8.0 / 3.0); // (n)(2)
                    } else {
                        textCount += (4.0 / 3.0); // (n)(3)
                    }
                }

                /* x12 ... step (o) */
                if (isX12(inputData[sp])) {
                    x12Count += (2.0 / 3.0); // (o)(1)
                } else {
                    if (inputData[sp] > 127) {
                        x12Count += (13.0 / 3.0); // (o)(2)
                    } else {
                        x12Count += (10.0 / 3.0); // (o)(3)
                    }
                }

                /* edifact ... step (p) */
                if ((inputData[sp] >= ' ') && (inputData[sp] <= '^')) {
                    edfCount += (3.0 / 4.0); // (p)(1)
                } else {
                    if (inputData[sp] > 127) {
                        edfCount += 17.0; // (p)(2) > Value changed from ISO
                    } else {
                        edfCount += 13.0; // (p)(3) > Value changed from ISO
                    }
                }
                if (inputData[sp] == FNC1) {
                    edfCount += 13.0; //  > Value changed from ISO
                }

                /* base 256 ... step (q) */
                if (inputData[sp] == FNC1) {
                    b256Count += 4.0; // (q)(1)
                } else {
                    b256Count += 1.0; // (q)(2)
                }
            }


            if (sp > (position + 3)) {
                /* 4 data characters processed ... step (r) */

                /* step (r)(6) */
                if (((c40Count + 1.0) < (asciiCount - stiction)) &&
                        ((c40Count + 1.0) < (b256Count - stiction)) &&
                        ((c40Count + 1.0) < (edfCount - stiction)) &&
                        ((c40Count + 1.0) < (textCount - stiction))) {

                    if (c40Count < (x12Count - stiction)) {
                        bestScheme = Mode.DM_C40;
                    }

                    if ((c40Count >= (x12Count - stiction))
                            && (c40Count <= (x12Count + stiction))) {
                        if (p_r_6_2_1(sp, sourcelen)) {
                            // Test (r)(6)(ii)(i)
                            bestScheme = Mode.DM_X12;
                        } else {
                            bestScheme = Mode.DM_C40;
                        }
                    }
                }

                /* step (r)(5) */
                if (((x12Count + 1.0) < (asciiCount - stiction)) &&
                        ((x12Count + 1.0) < (b256Count - stiction)) &&
                        ((x12Count + 1.0) < (edfCount - stiction)) &&
                        ((x12Count + 1.0) < (textCount - stiction)) &&
                        ((x12Count + 1.0) < (c40Count - stiction))) {
                    bestScheme = Mode.DM_X12;
                }

                /* step (r)(4) */
                if (((textCount + 1.0) < (asciiCount - stiction)) &&
                        ((textCount + 1.0) < (b256Count - stiction)) &&
                        ((textCount + 1.0) < (edfCount - stiction)) &&
                        ((textCount + 1.0) < (x12Count - stiction)) &&
                        ((textCount + 1.0) < (c40Count - stiction))) {
                    bestScheme = Mode.DM_TEXT;
                }

                /* step (r)(3) */
                if (((edfCount + 1.0) < (asciiCount - stiction)) &&
                        ((edfCount + 1.0) < (b256Count - stiction)) &&
                        ((edfCount + 1.0) < (textCount - stiction)) &&
                        ((edfCount + 1.0) < (x12Count - stiction)) &&
                        ((edfCount + 1.0) < (c40Count - stiction))) {
                    bestScheme = Mode.DM_EDIFACT;
                }

                /* step (r)(2) */
                if (((b256Count + 1.0) <= (asciiCount + stiction)) ||
                        (((b256Count + 1.0) < (edfCount - stiction)) &&
                                ((b256Count + 1.0) < (textCount - stiction)) &&
                                ((b256Count + 1.0) < (x12Count - stiction)) &&
                                ((b256Count + 1.0) < (c40Count - stiction)))) {
                    bestScheme = Mode.DM_BASE256;
                }

                /* step (r)(1) */
                if (((asciiCount + 1.0) <= (b256Count + stiction)) &&
                        ((asciiCount + 1.0) <= (edfCount + stiction)) &&
                        ((asciiCount + 1.0) <= (textCount + stiction)) &&
                        ((asciiCount + 1.0) <= (x12Count + stiction)) &&
                        ((asciiCount + 1.0) <= (c40Count + stiction))) {
                    bestScheme = Mode.DM_ASCII;
                }
            }

            sp++;

        } while (bestScheme == Mode.NULL); // step (s)

        return bestScheme;
    }

    private boolean p_r_6_2_1(int position, int sourcelen) {
        /* Annex P section (r)(6)(ii)(I)
           "If one of the three X12 terminator/separator characters first
            occurs in the yet to be processed data before a non-X12 character..."
        */

        int i;
        int nonX12Position = 0;
        int specialX12Position = 0;
        boolean retval = false;

        for (i = position; i < sourcelen; i++) {
            if (nonX12Position == 0 && !isX12(inputData[i])) {
                nonX12Position = i;
            }

            if (specialX12Position == 0) {
                if ((inputData[i] == (char) 13) ||
                        (inputData[i] == '*') ||
                        (inputData[i] == '>')) {
                    specialX12Position = i;
                }
            }
        }

        if ((nonX12Position != 0) && (specialX12Position != 0)) {
            if (specialX12Position < nonX12Position) {
                retval = true;
            }
        }

        return retval;
    }

    private boolean isX12(int source) {
        if (source == 13) {
            return true;
        }
        if (source == 42) {
            return true;
        }
        if (source == 62) {
            return true;
        }
        if (source == 32) {
            return true;
        }
        if ((source >= '0') && (source <= '9')) {
            return true;
        }
        if ((source >= 'A') && (source <= 'Z')) {
            return true;
        }

        return false;
    }

    private void calculateErrorCorrection(int bytes, int datablock, int rsblock, int skew) {
        // calculate and append ecc code, and if necessary interleave
        int blocks = (bytes + 2) / datablock, b;
        int n, p;
        ReedSolomon rs = new ReedSolomon();
        rs.init_gf(0x12d);
        rs.init_code(rsblock, 1);
        for (b = 0; b < blocks; b++) {
            int[] buf = new int[256];
            int[] ecc = new int[256];

            p = 0;
            for (n = b; n < bytes; n += blocks) {
                buf[p++] = target[n];
            }
            rs.encode(p, buf);
            for (n = 0; n < rsblock; n++) {
                ecc[n] = rs.getResult(n);
            }
            p = rsblock - 1; // comes back reversed
            for (n = b; n < rsblock * blocks; n += blocks) {
                if (skew == 1) {
                    /* Rotate ecc data to make 144x144 size symbols acceptable */
                    /* See http://groups.google.com/group/postscriptbarcode/msg/5ae8fda7757477da */
                    if (b < 8) {
                        target[bytes + n + 2] = ecc[p--];
                    } else {
                        target[bytes + n - 8] = ecc[p--];
                    }
                } else {
                    target[bytes + n] = ecc[p--];
                }
            }
        }
    }

    private void insertAt(int pos, char newbit) {
        /* Insert a character into the middle of a string at position posn */
        for (int i = binaryLength; i > pos; i--) {
            binary[i] = binary[i - 1];
        }
        binary[pos] = newbit;
        binaryLength++;
    }

    private void insertValueAt(int posn, int streamLength, char newbit) {
        int i;
        for (i = streamLength; i > posn; i--) {
            target[i] = target[i - 1];
        }
        target[posn] = newbit;
    }

    private void addPadBits(int tp, int tailLength) {
        int i, prn, temp;

        for (i = tailLength; i > 0; i--) {
            if (i == tailLength) {
                target[tp] = 129;
                tp++; /* Pad */
            } else {
                prn = ((149 * (tp + 1)) % 253) + 1;
                temp = 129 + prn;
                if (temp <= 254) {
                    target[tp] = temp;
                    tp++;
                } else {
                    target[tp] = temp - 254;
                    tp++;
                }
            }
        }
    }

    private void placeData(int NR, int NC) {
        int r, c, p;
        // invalidate
        for (r = 0; r < NR; r++) {
            for (c = 0; c < NC; c++) {
                places[r * NC + c] = 0;
            }
        }
        // start
        p = 1;
        r = 4;
        c = 0;
        do {
            // check corner
            if (r == NR && (c == 0)) {
                placeCornerA(NR, NC, p++);
            }
            if (r == NR - 2 && (c == 0) && ((NC % 4) != 0)) {
                placeCornerB(NR, NC, p++);
            }
            if (r == NR - 2 && (c == 0) && (NC % 8) == 4) {
                placeCornerC(NR, NC, p++);
            }
            if (r == NR + 4 && c == 2 && ((NC % 8) == 0)) {
                placeCornerD(NR, NC, p++);
            }
            // up/right
            do {
                if (r < NR && c >= 0 && (places[r * NC + c] == 0)) {
                    placeBlock(NR, NC, r, c, p++);
                }
                r -= 2;
                c += 2;
            } while (r >= 0 && c < NC);
            r++;
            c += 3;
            // down/left
            do {
                if (r >= 0 && c < NC && (places[r * NC + c] == 0)) {
                    placeBlock(NR, NC, r, c, p++);
                }
                r += 2;
                c -= 2;
            } while (r < NR && c >= 0);
            r += 3;
            c++;
        } while (r < NR || c < NC);
        // unfilled corner
        if (places[NR * NC - 1] == 0) {
            places[NR * NC - 1] = places[NR * NC - NC - 2] = 1;
        }
    }

    private void placeCornerA(int NR, int NC, int p) {
        placeBit(NR, NC, NR - 1, 0, p, 7);
        placeBit(NR, NC, NR - 1, 1, p, 6);
        placeBit(NR, NC, NR - 1, 2, p, 5);
        placeBit(NR, NC, 0, NC - 2, p, 4);
        placeBit(NR, NC, 0, NC - 1, p, 3);
        placeBit(NR, NC, 1, NC - 1, p, 2);
        placeBit(NR, NC, 2, NC - 1, p, 1);
        placeBit(NR, NC, 3, NC - 1, p, 0);
    }

    private void placeCornerB(int NR, int NC, int p) {
        placeBit(NR, NC, NR - 3, 0, p, 7);
        placeBit(NR, NC, NR - 2, 0, p, 6);
        placeBit(NR, NC, NR - 1, 0, p, 5);
        placeBit(NR, NC, 0, NC - 4, p, 4);
        placeBit(NR, NC, 0, NC - 3, p, 3);
        placeBit(NR, NC, 0, NC - 2, p, 2);
        placeBit(NR, NC, 0, NC - 1, p, 1);
        placeBit(NR, NC, 1, NC - 1, p, 0);
    }

    private void placeCornerC(int NR, int NC, int p) {
        placeBit(NR, NC, NR - 3, 0, p, 7);
        placeBit(NR, NC, NR - 2, 0, p, 6);
        placeBit(NR, NC, NR - 1, 0, p, 5);
        placeBit(NR, NC, 0, NC - 2, p, 4);
        placeBit(NR, NC, 0, NC - 1, p, 3);
        placeBit(NR, NC, 1, NC - 1, p, 2);
        placeBit(NR, NC, 2, NC - 1, p, 1);
        placeBit(NR, NC, 3, NC - 1, p, 0);
    }

    private void placeCornerD(int NR, int NC, int p) {
        placeBit(NR, NC, NR - 1, 0, p, 7);
        placeBit(NR, NC, NR - 1, NC - 1, p, 6);
        placeBit(NR, NC, 0, NC - 3, p, 5);
        placeBit(NR, NC, 0, NC - 2, p, 4);
        placeBit(NR, NC, 0, NC - 1, p, 3);
        placeBit(NR, NC, 1, NC - 3, p, 2);
        placeBit(NR, NC, 1, NC - 2, p, 1);
        placeBit(NR, NC, 1, NC - 1, p, 0);
    }

    private void placeBlock(int NR, int NC, int r, int c, int p) {
        placeBit(NR, NC, r - 2, c - 2, p, 7);
        placeBit(NR, NC, r - 2, c - 1, p, 6);
        placeBit(NR, NC, r - 1, c - 2, p, 5);
        placeBit(NR, NC, r - 1, c - 1, p, 4);
        placeBit(NR, NC, r - 1, c - 0, p, 3);
        placeBit(NR, NC, r - 0, c - 2, p, 2);
        placeBit(NR, NC, r - 0, c - 1, p, 1);
        placeBit(NR, NC, r - 0, c - 0, p, 0);
    }

    private void placeBit(int NR, int NC, int r, int c, int p, int b) {
        if (r < 0) {
            r += NR;
            c += 4 - ((NR + 4) % 8);
        }
        if (c < 0) {
            c += NC;
            r += 4 - ((NC + 4) % 8);
        }
        places[r * NC + c] = (p << 3) + b;
    }
}
