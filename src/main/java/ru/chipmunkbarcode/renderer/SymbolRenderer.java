package ru.chipmunkbarcode.renderer;

import ru.chipmunkbarcode.barcodeTypes.Symbol;

import java.io.IOException;

public interface SymbolRenderer {
    /**
     * Renders the specified symbology.
     *
     * @param symbol the symbology to render
     * @throws IOException if there is an I/O error
     */
    void render(Symbol symbol) throws IOException;

}
