import org.junit.Test;
import ru.chipmunkbarcode.barcodeTypes.Code128;
import ru.chipmunkbarcode.barcodeTypes.DataMatrix;
import ru.chipmunkbarcode.barcodeTypes.Symbol;
import ru.chipmunkbarcode.renderer.Java2DRenderer;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Test
 */

public class ImageOutputTest {

    String dMcode = "10460422900158721EDaAhsDSnw'X?9ljAtb";
    String dMcodeGS1 = "010460422900158721EDaAhsDSnw'X?\u001D93jAtb";
    String dMcode_GS1 = "015464564563645321.GH&\"iU\u001D8005000000\u001D93dGVz";
    String code128codeGS1 = "010460795029026810178\u001D112009151230914210100\u001D9401";
    String code128code_GS1 = "0104607950290268101781120091512309142101009401";
    int dpi = 300;

    @Test
    public void testDataMatrixGS1() throws IOException {
        int magnification = 10;
        DataMatrix symbol = new DataMatrix();
        symbol.setDataType(Symbol.DataType.GS1);
        symbol.setContent(dMcodeGS1);
        BufferedImage barcodeImage = new BufferedImage(symbol.getWidth() * magnification, symbol.getHeight() * magnification, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = barcodeImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Java2DRenderer renderer = new Java2DRenderer(g2d, magnification, Color.WHITE, Color.BLACK);
        renderer.render(symbol);
        ImageIO.write(barcodeImage, "PNG", new File("src/test/resources/img/DataMatrixGS1.PNG"));
    }

    @Test
    public void testDataMatrix() throws IOException {
        int magnification = 10;
        DataMatrix symbol = new DataMatrix();
        symbol.setContent(dMcode);
        BufferedImage barcodeImage = new BufferedImage(symbol.getWidth() * magnification, symbol.getHeight() * magnification, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = barcodeImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Java2DRenderer renderer = new Java2DRenderer(g2d, magnification, Color.WHITE, Color.BLACK);
        renderer.render(symbol);
        ImageIO.write(barcodeImage, "PNG", new File("src/test/resources/img/DataMatrix.PNG"));
    }

    @Test
    public void testCode128GS1() throws IOException {
        int magnification = 10;
        Code128 symbol = new Code128();
        symbol.setDataType(Symbol.DataType.GS1);
        symbol.setContent(code128code_GS1);
        BufferedImage barcodeImage = new BufferedImage(symbol.getWidth() * magnification, symbol.getHeight() * magnification, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = barcodeImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Java2DRenderer renderer = new Java2DRenderer(g2d, magnification, Color.WHITE, Color.BLACK);
        renderer.render(symbol);
        ImageIO.write(barcodeImage, "PNG", new File("src/test/resources/img/Code128GS1.PNG"));
    }

    @Test
    public void testCode128() throws IOException {
        int magnification = 10;
        Code128 symbol = new Code128();
        symbol.setContent(code128code_GS1);
        BufferedImage barcodeImage = new BufferedImage(symbol.getWidth() * magnification, symbol.getHeight() * magnification, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = barcodeImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Java2DRenderer renderer = new Java2DRenderer(g2d, magnification, Color.WHITE, Color.BLACK);
        renderer.render(symbol);
        ImageIO.write(barcodeImage, "PNG", new File("src/test/resources/img/Code128.PNG"));
    }
}
