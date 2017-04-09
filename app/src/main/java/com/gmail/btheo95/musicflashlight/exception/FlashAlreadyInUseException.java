package com.gmail.btheo95.musicflashlight.exception;

/**
 * Created by btheo on 4/9/2017.
 */

public class FlashAlreadyInUseException extends Exception {

    public FlashAlreadyInUseException() {
    }

    public FlashAlreadyInUseException(String message) {
        super(message);
    }

    public FlashAlreadyInUseException(Throwable cause) {
        super(cause);
    }

    public FlashAlreadyInUseException(String message, Throwable cause) {
        super(message, cause);
    }
}
