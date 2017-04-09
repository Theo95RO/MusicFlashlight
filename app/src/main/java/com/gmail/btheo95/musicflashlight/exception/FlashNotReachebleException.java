package com.gmail.btheo95.musicflashlight.exception;

/**
 * Created by btheo on 4/9/2017.
 */

public class FlashNotReachebleException extends Exception {

    public FlashNotReachebleException() {
    }

    public FlashNotReachebleException(String message) {
        super(message);
    }

    public FlashNotReachebleException(Throwable cause) {
        super(cause);
    }

    public FlashNotReachebleException(String message, Throwable cause) {
        super(message, cause);
    }
}
