package com.gmail.btheo95.musicflashlight.runnable;

import android.util.Log;

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

    private static final String TAG = StrobeRunnable.class.getSimpleName();

    protected boolean mFlashIsOn = false;
    protected volatile boolean mIsRunnableShutdown = false;
    protected boolean mShouldCloseResources = false;
    protected boolean mShouldTurnFlashOffAtShutdown = true;

    private OnStopListener mOnStopListener;
    private Strobe mStrobe;

    public StrobeRunnable() {
        mStrobe = Strobe.getInstance();
    }

    protected abstract void onStart() throws FlashAlreadyInUseException, CameraNotReachebleException, FlashNotReachebleException, MicNotReachebleException;

    protected void onPreStart() throws MicNotReachebleException, FlashNotReachebleException, CameraNotReachebleException, IOException {
        startResourcesIfNotStarted();
    }

    protected void onPostStart() throws FlashAlreadyInUseException {

        Log.d(TAG, "onPostStart()");
        if (mShouldTurnFlashOffAtShutdown) {
            Log.d(TAG, "onPostStart() - turning flash off");
            turnFlashOff();
        }
        if (mShouldCloseResources) {
            Log.d(TAG, "onPostStart() - stopping resources");
            stopResources();
        }
        notifyListener();
    }

    public void start() throws MicNotReachebleException, FlashNotReachebleException, CameraNotReachebleException, FlashAlreadyInUseException, IOException {
        onPreStart();
        onStart();
        onPostStart();
    }


    protected void startResources() throws IOException, CameraNotReachebleException, FlashNotReachebleException, MicNotReachebleException {
        mStrobe.start();
    }

    protected void stopResources() {
        mStrobe.stop();
//        notifyListener();
    }

    protected void startResourcesIfNotStarted() throws IOException, CameraNotReachebleException, FlashNotReachebleException, MicNotReachebleException {
        if (!mStrobe.isStarted()) {
            startResources();
        }
    }

//    public void shutdown(boolean shouldCloseResources) {
//        mShouldCloseResources = shouldCloseResources;
//        mIsRunnableShutdown = true;
//    }

    public void setShouldCloseResources(boolean shouldCloseResources) {
        mShouldCloseResources = shouldCloseResources;
    }

    public void setShouldTurnFlashOffAtShutDown(boolean shouldTurnFlashOffAtShutDown) {
        mShouldTurnFlashOffAtShutdown = shouldTurnFlashOffAtShutDown;
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

    protected int getMicrophoneAmplitude() {
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
