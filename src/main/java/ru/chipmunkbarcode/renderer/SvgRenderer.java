package ru.chipmunkbarcode.renderer;

import ru.chipmunkbarcode.barcodeTypes.Hexagon;
import ru.chipmunkbarcode.barcodeTypes.HumanReadableAlignment;
import ru.chipmunkbarcode.barcodeTypes.Symbol;
import ru.chipmunkbarcode.barcodeTypes.TextBox;
import org.w3c.dom.Document;
import org.w3c.dom.Text;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;

/**
 * Renders symbologies to SVG (Scalable Vector Graphics)
 */

public class SvgRenderer implements SymbolRenderer {
    /**
     * The output stream to render to.
     */
    private final OutputStream out;

    /**
     * The magnification factor to apply.
     */
    private final double magnification;

    /**
     * The paper (background) color.
     */
    private final Color paper;

    /**
     * The ink (foreground) color.
     */
    private final Color ink;

    /**
     * Whether or not to include the XML prolog in the output.
     */
    private final boolean xmlProlog;

    /**
     * Creates a new SVG renderer.
     *
     * @param out           the output stream to render to
     * @param magnification the magnification factor to apply
     * @param paper         the paper (background) color
     * @param ink           the ink (foreground) color
     * @param xmlProlog     whether or not to include the XML prolog in the output (usually {@code true} for
     *                      standalone SVG documents, {@code false} for SVG content embedded directly in HTML documents)
     */
    public SvgRenderer(OutputStream out, double magnification, Color paper, Color ink, boolean xmlProlog) {
        this.out = out;
        this.magnification = magnification;
        this.paper = paper;
        this.ink = ink;
        this.xmlProlog = xmlProlog;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void render(Symbol symbol) throws IOException {

        String content = symbol.getContent();
        int width = (int) (symbol.getWidth() * magnification);
        int height = (int) (symbol.getHeight() * magnification);
        int marginX = (int) (symbol.getQuietZoneHorizontal() * magnification);
        int marginY = (int) (symbol.getQuietZoneVertical() * magnification);

        String title;
        if (content == null || content.isEmpty()) {
            title = "OkapiBarcode Generated Symbol";
        } else {
            title = content;
        }

        String fgColour = String.format("%02X", ink.getRed())
                + String.format("%02X", ink.getGreen())
                + String.format("%02X", ink.getBlue());

        String bgColour = String.format("%02X", paper.getRed())
                + String.format("%02X", paper.getGreen())
                + String.format("%02X", paper.getBlue());

        try (ExtendedOutputStreamWriter writer = new ExtendedOutputStreamWriter(out, "%.2f")) {

            // XML Prolog
            if (xmlProlog) {
                writer.append("<?xml version=\"1.0\" standalone=\"no\"?>\n");
                writer.append("<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\"\n");
                writer.append("   \"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\">\n");
            }

            // Header
            writer.append("<svg width=\"").appendInt(width)
                    .append("\" height=\"").appendInt(height)
                    .append("\" version=\"1.1")
                    .append("\" xmlns=\"http://www.w3.org/2000/svg\">\n");
            writer.append("   <desc>").append(clean(title)).append("</desc>\n");
            writer.append("   <g id=\"barcode\" fill=\"#").append(fgColour).append("\">\n");
            writer.append("      <rect x=\"0\" y=\"0\" width=\"").appendInt(width)
                    .append("\" height=\"").appendInt(height)
                    .append("\" fill=\"#").append(bgColour).append("\" />\n");

            // Rectangles
            for (int i = 0; i < symbol.getRectangles().size(); i++) {
                Rectangle2D.Double rect = symbol.getRectangles().get(i);
                writer.append("      <rect x=\"").append((rect.x * magnification) + marginX)
                        .append("\" y=\"").append((rect.y * magnification) + marginY)
                        .append("\" width=\"").append(rect.width * magnification)
                        .append("\" height=\"").append(rect.height * magnification)
                        .append("\" />\n");
            }

            // Text
            for (int i = 0; i < symbol.getTexts().size(); i++) {
                TextBox text = symbol.getTexts().get(i);
                HumanReadableAlignment alignment = (text.alignment == HumanReadableAlignment.JUSTIFY && text.text.length() == 1 ? HumanReadableAlignment.CENTER : text.alignment);
                double x;
                String anchor;
                switch (alignment) {
                    case LEFT:
                    case JUSTIFY:
                        x = (magnification * text.x) + marginX;
                        anchor = "start";
                        break;
                    case RIGHT:
                        x = (magnification * text.x) + (magnification * text.width) + marginX;
                        anchor = "end";
                        break;
                    case CENTER:
                        x = (magnification * text.x) + (magnification * text.width / 2) + marginX;
                        anchor = "middle";
                        break;
                    default:
                        throw new IllegalStateException("Unknown alignment: " + alignment);
                }
                writer.append("      <text x=\"").append(x)
                        .append("\" y=\"").append((text.y * magnification) + marginY)
                        .append("\" text-anchor=\"").append(anchor).append("\"\n");
                if (alignment == HumanReadableAlignment.JUSTIFY) {
                    writer.append("         textLength=\"")
                            .append(text.width * magnification)
                            .append("\" lengthAdjust=\"spacing\"\n");
                }
                writer.append("         font-family=\"").append(clean(symbol.getFontName()))
                        .append("\" font-size=\"").append(symbol.getFontSize() * magnification)
                        .append("\" fill=\"#").append(fgColour).append("\">\n");
                writer.append("         ").append(clean(text.text)).append("\n");
                writer.append("      </text>\n");
            }

            // Circles
            for (int i = 0; i < symbol.getTarget().size(); i++) {
                Ellipse2D.Double ellipse = symbol.getTarget().get(i);
                String color;
                if ((i & 1) == 0) {
                    color = fgColour;
                } else {
                    color = bgColour;
                }
                writer.append("      <circle cx=\"").append(((ellipse.x + (ellipse.width / 2)) * magnification) + marginX)
                        .append("\" cy=\"").append(((ellipse.y + (ellipse.width / 2)) * magnification) + marginY)
                        .append("\" r=\"").append((ellipse.width / 2) * magnification)
                        .append("\" fill=\"#").append(color).append("\" />\n");
            }

            // Hexagons
            for (int i = 0; i < symbol.getHexagons().size(); i++) {
                Hexagon hexagon = symbol.getHexagons().get(i);
                writer.append("      <path d=\"");
                for (int j = 0; j < 6; j++) {
                    if (j == 0) {
                        writer.append("M ");
                    } else {
                        writer.append("L ");
                    }
                    writer.append((hexagon.pointX[j] * magnification) + marginX).append(" ")
                            .append((hexagon.pointY[j] * magnification) + marginY).append(" ");
                }
                writer.append("Z\" />\n");
            }

            // Footer
            writer.append("   </g>\n");
            writer.append("</svg>\n");
        }
    }

    /**
     * Cleans / sanitizes the specified string for inclusion in XML. A bit convoluted, but we're
     * trying to do it without adding an external dependency just for this...
     *
     * @param s the string to be cleaned / sanitized
     * @return the cleaned / sanitized string
     */
    protected String clean(String s) {

        // remove control characters
        s = s.replaceAll("[\u0000-\u001f]", "");

        // escape XML characters
        try {
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            Text text = document.createTextNode(s);
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            DOMSource source = new DOMSource(text);
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.transform(source, result);
            return writer.toString();
        } catch (ParserConfigurationException | TransformerException | TransformerFactoryConfigurationError e) {
            return s;
        }
    }
}
