package com.gmail.btheo95.musicflashlight.exception;

/**
 * Created by btheo on 4/9/2017.
 */

public class CameraNotReachebleException extends Exception {

    public CameraNotReachebleException() {
    }

    public CameraNotReachebleException(String message) {
        super(message);
    }

    public CameraNotReachebleException(Throwable cause) {
        super(cause);
    }

    public CameraNotReachebleException(String message, Throwable cause) {
        super(message, cause);
    }
}
