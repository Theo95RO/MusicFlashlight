package com.gmail.btheo95.musicflashlight.resource;

import com.gmail.btheo95.musicflashlight.exception.CameraNotReachebleException;
import com.gmail.btheo95.musicflashlight.exception.FlashAlreadyInUseException;
import com.gmail.btheo95.musicflashlight.exception.FlashNotReachebleException;
import com.gmail.btheo95.musicflashlight.exception.MicNotReachebleException;

import java.io.IOException;

/**
 * Created by btheo on 3/28/2017.
 */

public final class Strobe {

    private static Strobe strobe = new Strobe();
    private boolean mFlashIsOn = false;
    private FlashCamera mCamera = null;
    private SoundMeter mSoundMeter = null;
    private boolean mIsStarted = false;

    private Strobe() {
    }

    public static Strobe getInstance() {
        return strobe;
    }

    public synchronized void start() throws IOException, CameraNotReachebleException, FlashNotReachebleException, MicNotReachebleException {
        if (mIsStarted) {
            return;
        }
        startSoundMeter();
        startCamera();
        mIsStarted = true;
    }

    public synchronized void stop() {
        if (!mIsStarted) {
            return;
        }
        stopCamera();
        stopSoundMeter();
        mIsStarted = false;
    }

    private void startSoundMeter() throws IOException, MicNotReachebleException {
        mSoundMeter = new SoundMeter();
        mSoundMeter.start();
    }

    private void stopSoundMeter() {
        if (mSoundMeter == null) {
            return;
        }
        mSoundMeter.stop();
    }

    private void startCamera() throws IOException, CameraNotReachebleException, FlashNotReachebleException {
        mCamera = new FlashCamera();
        mCamera.startCamera();
    }

    private void stopCamera() {
        if (mCamera == null) {
            return;
        }
        mCamera.stopCamera();
    }

    public synchronized void toggleFlash() throws FlashAlreadyInUseException {
        mCamera.toggleFlash();
        mFlashIsOn = mCamera.isFlashOn();
    }

    public synchronized void turnFlashOn() throws FlashAlreadyInUseException {
        mCamera.turnFlashOn();
        mFlashIsOn = true;
    }

    public synchronized void turnFlashOff() throws FlashAlreadyInUseException {
        mCamera.turnFlashOff();
        mFlashIsOn = false;
    }

    public synchronized boolean isStarted() {
        return mIsStarted;
    }

    public synchronized boolean isFlashOn() {
        return mFlashIsOn;
    }

    public synchronized int getMicrophoneAmplitude() {
        return mSoundMeter.getAmplitude();
    }
}

