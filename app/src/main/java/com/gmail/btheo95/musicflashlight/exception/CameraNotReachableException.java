package com.gmail.btheo95.musicflashlight.exception;

/**
 * Created by btheo on 4/9/2017.
 */

public class CameraNotReachableException extends Exception {

    public CameraNotReachableException() {
    }

    public CameraNotReachableException(String message) {
        super(message);
    }

    public CameraNotReachableException(Throwable cause) {
        super(cause);
    }

    public CameraNotReachableException(String message, Throwable cause) {
        super(message, cause);
    }
}
