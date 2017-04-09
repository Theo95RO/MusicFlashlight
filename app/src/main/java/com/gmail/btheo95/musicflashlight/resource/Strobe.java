package com.gmail.btheo95.musicflashlight.resource;

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

    public Strobe() {
    }

    public static Strobe getInstance() {
        return strobe;
    }

    public synchronized void start() throws IOException {
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

    private void startSoundMeter() throws IOException {
        mSoundMeter = new SoundMeter();
        mSoundMeter.start();
    }

    private void stopSoundMeter() {
        if (mSoundMeter == null) {
            return;
        }
        mSoundMeter.stop();
    }

    private void startCamera() throws IOException {
        mCamera = new FlashCamera();
        mCamera.startCamera();
    }

    private void stopCamera() {
        if (mCamera == null) {
            return;
        }
        mCamera.stopCamera();
    }

    public synchronized void toggleFlash() {
        mCamera.toggleFlash();
        mFlashIsOn = mCamera.isFlashOn();
    }

    public synchronized void turnFlashOn() {
        mCamera.turnFlashOn();
        mFlashIsOn = true;
    }

    public synchronized void turnFlashOff() {
        mCamera.turnFlashOff();
        mFlashIsOn = false;
    }

    public synchronized boolean isStarted() {
        return mIsStarted;
    }

    public synchronized boolean isFlashOn() {
        return mFlashIsOn;
    }

    public synchronized double getMicrophoneAmplitude() {
        return mSoundMeter.getAmplitude();
    }
}

