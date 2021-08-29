package ru.chipmunkbarcode.exceptions;

public class BarcodeException extends RuntimeException {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = -630504124631140642L;

    /**
     * Creates a new instance.
     *
     * @param message the error message
     */
    public BarcodeException(String message) {
        super(message);
    }
}
