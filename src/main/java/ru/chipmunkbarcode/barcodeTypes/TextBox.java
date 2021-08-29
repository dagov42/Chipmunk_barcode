
package ru.chipmunkbarcode.barcodeTypes;

/**
 * A simple text item class.
 */
public class TextBox {

    /**
     * The X position of the text's left boundary.
     */
    public final double x;

    /**
     * The Y position of the text baseline.
     */
    public final double y;

    /**
     * The width of the text box.
     */
    public final double width;

    /**
     * The text value.
     */
    public final String text;

    /**
     * The text alignment.
     */
    public final HumanReadableAlignment alignment;

    /**
     * Creates a new instance.
     *
     * @param x         the X position of the text's left boundary
     * @param y         the Y position of the text baseline
     * @param width     the width of the text box
     * @param text      the text value
     * @param alignment the text alignment
     */
    public TextBox(double x, double y, double width, String text, HumanReadableAlignment alignment) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.text = text;
        this.alignment = alignment;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "TextBox[x=" + x + ", y=" + y + ", width=" + width + ", text=" + text + ", alignment=" + alignment + "]";
    }
}
