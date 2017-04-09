package com.gmail.btheo95.musicflashlight.runnable;

import com.gmail.btheo95.musicflashlight.resource.Strobe;

import java.io.IOException;

/**
 * Created by btheo on 3/28/2017.
 */

public abstract class StrobeRunnable implements Runnable {


    protected boolean mFlashIsOn = false;
    protected volatile boolean mIsRunnableShutdown = false;
    protected boolean mShouldCloseResources = true;
    private OnStopListener mOnStopListener;
    private Strobe mStrobe;

    public StrobeRunnable() {
        mStrobe = Strobe.getInstance();
    }


    protected void startResources() throws IOException {
        mStrobe.start();
    }

    protected void stopResources() {
        mStrobe.stop();
        notifyListener();
    }

    protected void startResourcesIfNotStarted() throws IOException {
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

    protected void toggleFlash() {
        mStrobe.toggleFlash();
        mFlashIsOn = mStrobe.isFlashOn();
    }

    protected void turnFlashOn() {
        mStrobe.turnFlashOn();
        mFlashIsOn = true;
    }

    protected void turnFlashOff() {
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
