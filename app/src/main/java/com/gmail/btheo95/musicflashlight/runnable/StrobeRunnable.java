package com.gmail.btheo95.musicflashlight.runnable;

import com.gmail.btheo95.musicflashlight.exception.CameraNotReachebleException;
import com.gmail.btheo95.musicflashlight.exception.FlashAlreadyInUseException;
import com.gmail.btheo95.musicflashlight.exception.FlashNotReachebleException;
import com.gmail.btheo95.musicflashlight.exception.MicNotReachebleException;
import com.gmail.btheo95.musicflashlight.resource.Strobe;

import java.io.IOException;

/**
 * Created by btheo on 3/28/2017.
 */

public abstract class StrobeRunnable {


    protected boolean mFlashIsOn = false;
    protected volatile boolean mIsRunnableShutdown = false;
    protected boolean mShouldCloseResources = true;
    private OnStopListener mOnStopListener;
    private Strobe mStrobe;

    public StrobeRunnable() {
        mStrobe = Strobe.getInstance();
    }

    public abstract void run() throws FlashAlreadyInUseException, CameraNotReachebleException, FlashNotReachebleException, MicNotReachebleException;

    protected void startResources() throws IOException, CameraNotReachebleException, FlashNotReachebleException, MicNotReachebleException {
        mStrobe.start();
    }

    protected void stopResources() {
        mStrobe.stop();
        notifyListener();
    }

    protected void startResourcesIfNotStarted() throws IOException, CameraNotReachebleException, FlashNotReachebleException, MicNotReachebleException {
        if (!mStrobe.isStarted()) {
            startResources();
        }
    }

    public void shutdown(boolean shouldCloseResources) {
        mShouldCloseResources = shouldCloseResources;
        mIsRunnableShutdown = true;
    }

    public void shutdown() {
        //Thread.currentThread().interrupt();
        mIsRunnableShutdown = true;
    }

    protected void toggleFlash() throws FlashAlreadyInUseException {
        mStrobe.toggleFlash();
        mFlashIsOn = mStrobe.isFlashOn();
    }

    protected void turnFlashOn() throws FlashAlreadyInUseException {
        mStrobe.turnFlashOn();
        mFlashIsOn = true;
    }

    protected void turnFlashOff() throws FlashAlreadyInUseException {
        mStrobe.turnFlashOff();
        mFlashIsOn = false;
    }

    protected double getMicrophoneAmplitude() {
        return mStrobe.getMicrophoneAmplitude();
    }

    public void setOnStopListener(StrobeRunnable.OnStopListener listener) {
        mOnStopListener = listener;
    }

    protected void notifyListener() {
        if (mOnStopListener != null) {
            mOnStopListener.onStop();
        }
    }

    public interface OnStopListener {
        void onStop();
    }
}
