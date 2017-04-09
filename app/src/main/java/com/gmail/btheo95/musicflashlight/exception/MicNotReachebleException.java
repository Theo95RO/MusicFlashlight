package com.gmail.btheo95.musicflashlight.exception;

/**
 * Created by btheo on 4/9/2017.
 */

public class MicNotReachebleException extends Exception {

    public MicNotReachebleException() {
    }

    public MicNotReachebleException(String message) {
        super(message);
    }

    public MicNotReachebleException(Throwable cause) {
        super(cause);
    }

    public MicNotReachebleException(String message, Throwable cause) {
        super(message, cause);
    }
}
