import org.junit.Test;
import ru.chipmunkbarcode.barcodeTypes.Code128;
import ru.chipmunkbarcode.barcodeTypes.DataMatrix;
import ru.chipmunkbarcode.barcodeTypes.Symbol;
import ru.chipmunkbarcode.renderer.SvgRenderer;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Test
 */

public class SVGOutputTest {

    String dMcode = "10460422900158721EDaAhsDSnw'X?9ljAtb";
    String dMcodeGS1 = "010460422900158721EDaAhsDSnw'X?\u001D93jAtb";
    String dMcode_GS1 = "015464564563645321.GH&\"iU\u001D8005000000\u001D93dGVz";
    String code128codeGS1 = "010460795029026810178\u001D112009151230914210100\u001D9401";
    String code128code_GS1 = "0104607950290268101781120091512309142101009401";

    @Test
    public void testDataMatrixGS1() throws IOException {
        int magnification = 20;
        DataMatrix symbol = new DataMatrix();
        symbol.setDataType(Symbol.DataType.GS1);
        symbol.setContent(dMcodeGS1);
        File file = new File("src/test/resources/svg", "DataMatrixGS1.svg");
        SvgRenderer renderer = new SvgRenderer(new FileOutputStream(file), magnification, Color.WHITE, Color.BLACK, true);
        renderer.render(symbol);
    }

    @Test
    public void testDataMatrix() throws IOException {
        int magnification = 20;
        DataMatrix symbol = new DataMatrix();
        symbol.setContent(dMcode);
        File file = new File("src/test/resources/svg", "DataMatrix.svg");
        SvgRenderer renderer = new SvgRenderer(new FileOutputStream(file), magnification, Color.WHITE, Color.BLACK, true);
        renderer.render(symbol);
    }

    @Test
    public void testCode128GS1() throws IOException {
        int magnification = 20;
        Code128 symbol = new Code128();
        symbol.setDataType(Symbol.DataType.GS1);
        symbol.setContent(code128code_GS1);
        File file = new File("src/test/resources/svg", "Code128GS1.svg");
        SvgRenderer renderer = new SvgRenderer(new FileOutputStream(file), magnification, Color.WHITE, Color.BLACK, true);
        renderer.render(symbol);
    }
}
