package com.gmail.btheo95.musicflashlight.runnable;

import android.util.Log;

import com.gmail.btheo95.musicflashlight.exception.CameraNotReachebleException;
import com.gmail.btheo95.musicflashlight.exception.FlashAlreadyInUseException;
import com.gmail.btheo95.musicflashlight.exception.FlashNotReachebleException;
import com.gmail.btheo95.musicflashlight.exception.MicNotReachebleException;
import com.gmail.btheo95.musicflashlight.util.Constants;

import java.util.concurrent.TimeUnit;

/**
 * Created by btheo on 3/16/2017.
 */

public final class MusicStrobe extends StrobeRunnable {

    private static final String TAG = MusicStrobe.class.getSimpleName();

    private static final int MIN_AVERAGE_AMPLITUDE = 150;
    private static final float THRESHOLD_COEFFICIENT = 1.28f;
    private static final float AMPLITUDE_CORRECTION_COEFFICIENT = 0.14f;
    private static final int THREAD_WAITING_TIME = 50;
    private static final int MAX_AVERAGE_AMPLITUDE = (int) (32767 - (THRESHOLD_COEFFICIENT - 1) * 32767); // related to THRESHOLD_COEFFICIENT

    private float mManualThreshold = Constants.MUSIC_SEEK_BAR_DEFAULT_VALUE;
    private int mAverageAmplitude;
    private boolean mAutoThreshold;

    public MusicStrobe() {
        mAutoThreshold = true;
        mAverageAmplitude = 1000;
    }

    public MusicStrobe(boolean autoThreshold, int threshold) {
        mAutoThreshold = autoThreshold;
        this.mManualThreshold = threshold;
    }

    @Override
    public void onStart() throws FlashAlreadyInUseException, CameraNotReachebleException, FlashNotReachebleException, MicNotReachebleException {

        try {
            while (!mIsRunnableShutdown.get()) {

                int currentAmplitude = getMicrophoneAmplitude();
                if (currentAmplitude > calculateThreshold()) {
                    turnFlashOn();
                } else {
                    turnFlashOff();
                }

                if (mAutoThreshold) {
                    calculateAverageAmplitude(currentAmplitude);
                    Log.v(TAG, "current amplitude: " + currentAmplitude);
                    Log.v(TAG, "average amplitude: " + mAverageAmplitude);
                }

                TimeUnit.MILLISECONDS.sleep(THREAD_WAITING_TIME);
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private float calculateThreshold() {
        if (mAutoThreshold) {
            return mAverageAmplitude * THRESHOLD_COEFFICIENT;
        } else {
            return mManualThreshold;
        }
    }

    private void calculateAverageAmplitude(int currentAmplitude) {
        mAverageAmplitude -= mAverageAmplitude * AMPLITUDE_CORRECTION_COEFFICIENT;
        mAverageAmplitude += currentAmplitude * AMPLITUDE_CORRECTION_COEFFICIENT;

        if (mAverageAmplitude > MAX_AVERAGE_AMPLITUDE) {
            mAverageAmplitude = MAX_AVERAGE_AMPLITUDE;
        } else if (mAverageAmplitude < MIN_AVERAGE_AMPLITUDE) {
            mAverageAmplitude = MIN_AVERAGE_AMPLITUDE;
        }
    }

    public synchronized void setAutoThreshold() {
        this.mAutoThreshold = true;
    }

    public synchronized void setManualThreshold(int threshold) {
        this.mManualThreshold = threshold;
        this.mAutoThreshold = false;
    }

    public synchronized void setThreshold(int threshold){
        this.mManualThreshold = threshold;
    }
}