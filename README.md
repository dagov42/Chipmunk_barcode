# Chipmunk Barcode 
Chipmunk Barcode is an open-source barcode generator written entirely in Java,
supporting over 50 encoding standards, including all ISO and GS1 standards. Chipmunk
Barcode is based on [Okapi Barcode](https://github.com/woo-j/OkapiBarcode) and [Zint](https://sourceforge.net/projects/zint/) an
open-source barcode encoding library developed in C, and builds on the years
of work that have been invested in that project. GS1 formatter library based on [GS1 utils](https://github.com/tmattsson/gs1utils)
that is Java library for GS1 data structures commonly used in barcodes, such as GTIN, GLN, SSCC and element strings. Provides methods for validation and conversion between different length GTINs.

### Supported Symbologies

* [Australia Post](src/main/java/ru/chipmunkbarcode/barcodeTypes/AustraliaPost.java) variants:
  * Standard Customer
  * Reply Paid
  * Routing
  * Redirection
* [Aztec Code](src/main/java/ru/chipmunkbarcode/barcodeTypes/AztecCode.java)
* [Aztec Runes](src/main/java/ru/chipmunkbarcode/barcodeTypes/AztecRune.java)
* [Channel Code](src/main/java/ru/chipmunkbarcode/barcodeTypes/ChannelCode.java)
* [Codabar](src/main/java/ru/chipmunkbarcode/barcodeTypes/Codabar.java)
* [Codablock F](src/main/java/ru/chipmunkbarcode/barcodeTypes/CodablockF.java)
* [Code 11](src/main/java/ru/chipmunkbarcode/barcodeTypes/Code11.java)
* [Code 128](src/main/java/ru/chipmunkbarcode/barcodeTypes/Code128.java)
* [Code 16k](src/main/java/ru/chipmunkbarcode/barcodeTypes/Code16k.java)
* [Code 2 of 5](src/main/java/ru/chipmunkbarcode/barcodeTypes/Code2Of5.java) variants:
  * Matrix 2 of 5
  * Industrial 2 of 5
  * IATA 2 of 5
  * Datalogic 2 of 5
  * Interleaved 2 of 5
  * ITF-14
  * Deutsche Post Leitcode
  * Deutsche Post Identcode
* [Code 32](src/main/java/ru/chipmunkbarcode/barcodeTypes/Code32.java) (Italian pharmacode)
* [Code 3 of 9](src/main/java/ru/chipmunkbarcode/barcodeTypes/Code3Of9.java) (Code 39)
* [Code 3 of 9 Extended](src/main/java/ru/chipmunkbarcode/barcodeTypes/Code3Of9Extended.java) (Code 39 Extended)
* [Code 49](src/main/java/ru/chipmunkbarcode/barcodeTypes/Code49.java)
* [Code 93](src/main/java/ru/chipmunkbarcode/barcodeTypes/Code93.java)
* [Code One](src/main/java/ru/chipmunkbarcode/barcodeTypes/CodeOne.java)
* [Composite](src/main/java/ru/chipmunkbarcode/barcodeTypes/Composite.java)
* [Data Matrix](src/main/java/ru/chipmunkbarcode/barcodeTypes/DataMatrix.java)
* [Dutch Post KIX Code](src/main/java/ru/chipmunkbarcode/barcodeTypes/KixCode.java)
* [EAN](src/main/java/ru/chipmunkbarcode/barcodeTypes/Ean.java) variants:
  * EAN-13
  * EAN-8
* [Grid Matrix](src/main/java/ru/chipmunkbarcode/barcodeTypes/GridMatrix.java)
* [GS1 DataBar](src/main/java/ru/chipmunkbarcode/barcodeTypes/DataBar14.java) variants:
  * GS1 DataBar
  * GS1 DataBar Stacked
  * GS1 DataBar Stacked Omnidirectional
* [GS1 DataBar Expanded](src/main/java/ru/chipmunkbarcode/barcodeTypes/DataBarExpanded.java) variants:
  * GS1 DataBar Expanded
  * GS1 DataBar Expanded Stacked
* [GS1 DataBar Limited](src/main/java/ru/chipmunkbarcode/barcodeTypes/DataBarLimited.java)
* [Japan Post](src/main/java/ru/chipmunkbarcode/barcodeTypes/JapanPost.java)
* [Korea Post](src/main/java/ru/chipmunkbarcode/barcodeTypes/KoreaPost.java)
* [LOGMARS](src/main/java/ru/chipmunkbarcode/barcodeTypes/Logmars.java)
* [MaxiCode](src/main/java/ru/chipmunkbarcode/barcodeTypes/MaxiCode.java)
* [MSI](src/main/java/ru/chipmunkbarcode/barcodeTypes/MsiPlessey.java) (Modified Plessey)
* [PDF417](src/main/java/ru/chipmunkbarcode/barcodeTypes/Pdf417.java) variants:
  * PDF417
  * Truncated PDF417
  * Micro PDF417
* [Pharmacode](src/main/java/ru/chipmunkbarcode/barcodeTypes/Pharmacode.java)
* [Pharmacode Two-Track](src/main/java/ru/chipmunkbarcode/barcodeTypes/Pharmacode2Track.java)
* [POSTNET / PLANET](src/main/java/ru/chipmunkbarcode/barcodeTypes/Postnet.java)
* [QR Code](src/main/java/ru/chipmunkbarcode/barcodeTypes/QrCode.java)
* [Royal Mail 4 State](src/main/java/ru/chipmunkbarcode/barcodeTypes/RoyalMail4State.java) (RM4SCC)
* [Telepen](src/main/java/ru/chipmunkbarcode/barcodeTypes/Telepen.java) variants:
  * Telepen
  * Telepen Numeric
* [UPC](src/main/java/ru/chipmunkbarcode/barcodeTypes/Upc.java) variants:
  * UPC-A
  * UPC-E
* [USPS OneCode](src/main/java/ru/chipmunkbarcode/barcodeTypes/UspsOneCode.java) (Intelligent Mail)

### Library Usage

To generate barcode images in your own code using the Chipmunk Barcode library, use one of the symbology
classes linked above:

1. instantiate the class,
2. customize any relevant settings,
3. invoke `setContent(String)`, and then
4. pass the symbol instance to one of the available symbol renderers
([Java 2D](src/main/java/ru/chipmunkbarcode/renderer/Java2DRenderer.java),
[PostScript](src/main/java/ru/chipmunkbarcode/renderer/PostScriptRenderer.java),
[SVG](src/main/java/ru/chipmunkbarcode/renderer/SvgRenderer.java))

```
Code128 barcode = new Code128();
barcode.setFontName("Monospaced");
barcode.setFontSize(16);
barcode.setModuleWidth(2);
barcode.setBarHeight(50);
barcode.setHumanReadableLocation(HumanReadableLocation.BOTTOM);
barcode.setContent("123456789");

int width = barcode.getWidth();
int height = barcode.getHeight();

BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
Graphics2D g2d = image.createGraphics();
Java2DRenderer renderer = new Java2DRenderer(g2d, 1, Color.WHITE, Color.BLACK);
renderer.render(barcode);

ImageIO.write(image, "png", new File("code128.png"));

```