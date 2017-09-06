package com.gmail.btheo95.musicflashlight.exception;

/**
 * Created by btheo on 4/9/2017.
 */

public class FlashNotReachableException extends Exception {

    public FlashNotReachableException() {
    }

    public FlashNotReachableException(String message) {
        super(message);
    }

    public FlashNotReachableException(Throwable cause) {
        super(cause);
    }

    public FlashNotReachableException(String message, Throwable cause) {
        super(message, cause);
    }
}
