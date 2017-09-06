package com.gmail.btheo95.musicflashlight.exception;

/**
 * Created by btheo on 4/9/2017.
 */

public class MicNotReachableException extends Exception {

    public MicNotReachableException() {
    }

    public MicNotReachableException(String message) {
        super(message);
    }

    public MicNotReachableException(Throwable cause) {
        super(cause);
    }

    public MicNotReachableException(String message, Throwable cause) {
        super(message, cause);
    }
}
