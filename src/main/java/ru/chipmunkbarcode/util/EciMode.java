package ru.chipmunkbarcode.util;

import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;

public class EciMode {

    public static final EciMode NONE = new EciMode(-1, null);

    public final int mode;
    public final Charset charset;

    private EciMode(int mode, Charset charset) {
        this.mode = mode;
        this.charset = charset;
    }

    public static EciMode of(String data, String charsetName, int mode) {
        try {
            Charset charset = Charset.forName(charsetName);
            if (charset.canEncode() && charset.newEncoder().canEncode(data)) {
                return new EciMode(mode, charset);
            } else {
                return NONE;
            }
        } catch (UnsupportedCharsetException e) {
            return NONE;
        }
    }

    public EciMode or(String data, String charsetName, int mode) {
        if (!this.equals(NONE)) {
            return this;
        } else {
            return of(data, charsetName, mode);
        }
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof EciMode && ((EciMode) other).mode == this.mode;
    }

    @Override
    public int hashCode() {
        return Integer.valueOf(mode).hashCode();
    }

    @Override
    public String toString() {
        return "EciMode[mode=" + mode + ", charset=" + charset + "]";
    }
}
