package com.gmail.btheo95.musicflashlight.runnable;

import android.util.Log;

import com.gmail.btheo95.musicflashlight.exception.CameraNotReachebleException;
import com.gmail.btheo95.musicflashlight.exception.FlashAlreadyInUseException;
import com.gmail.btheo95.musicflashlight.exception.FlashNotReachebleException;
import com.gmail.btheo95.musicflashlight.exception.MicNotReachebleException;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Created by btheo on 3/16/2017.
 */

public final class MusicStrobe extends StrobeRunnable {

    private static final String TAG = MusicStrobe.class.getSimpleName();

    private static final float AMPLITUDE_CORRECTION_COEFFICIENT = 0.15f;
    //    private static final double AMPLITUDE_CORRECTION_COEFFICIENT = 0.10;
    private static final float THRESHOLD_COEFFICIENT = 1.25f;
    private static final int THREAD_WAITING_TIME = 50;
    private static final int MAX_AVERAGE_AMPLITUDE = (int) (32767 - (THRESHOLD_COEFFICIENT -1 ) * 32767); // related to THRESHOLD_COEFFICIENT
    private static final int MIN_AVERAGE_AMPLITUDE = 125;
//    private static final int THREAD_WAITING_TIME = 25;

    private int mAverageAmplitude;
    private boolean mAutoThreshold;

    public MusicStrobe() {
        mAutoThreshold = true;
        mAverageAmplitude = 100;
    }

    public MusicStrobe(boolean autoThreshold, int initialAmplitude) {
        mAutoThreshold = autoThreshold;
        mAverageAmplitude = initialAmplitude;
    }

    @Override
    public void onStart() throws FlashAlreadyInUseException, CameraNotReachebleException, FlashNotReachebleException, MicNotReachebleException {

        try {
            while (!mIsRunnableShutdown.get()) {

                int currentAmplitude = getMicrophoneAmplitude();
                if (currentAmplitude > mAverageAmplitude * THRESHOLD_COEFFICIENT) {
                    turnFlashOn();
                } else {
                    turnFlashOff();
                }
                if (mAutoThreshold) {
                    calculateAutoThreshold(currentAmplitude);
                }

                Log.v(TAG, "current amplitude: " + currentAmplitude);
                Log.v(TAG, "average amplitude: " + mAverageAmplitude);

                TimeUnit.MILLISECONDS.sleep(THREAD_WAITING_TIME);
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void calculateAutoThreshold(int currentAmplitude) {
        mAverageAmplitude -= mAverageAmplitude * AMPLITUDE_CORRECTION_COEFFICIENT;
        mAverageAmplitude += currentAmplitude * AMPLITUDE_CORRECTION_COEFFICIENT;

        if (mAverageAmplitude > MAX_AVERAGE_AMPLITUDE) {
            mAverageAmplitude = MAX_AVERAGE_AMPLITUDE;
        } else if (mAverageAmplitude < MIN_AVERAGE_AMPLITUDE) {
            mAverageAmplitude = MIN_AVERAGE_AMPLITUDE;
        }
//        double correction = currentAmplitude * AMPLITUDE_CORRECTION_COEFFICIENT;
//        if (currentAmplitude > mAverageAmplitude) {
//            mAverageAmplitude += correction;
//        } else {
//            mAverageAmplitude -= correction;
//        }
    }

    public int getThreshold() {
        return mAverageAmplitude;
    }

    public void setThreshold(int threshold) {
        this.mAverageAmplitude = threshold;
    }

    public boolean isAutoThreshold() {
        return mAutoThreshold;
    }

    public void setAutoThreshold(boolean autoThreshold) {
        this.mAutoThreshold = autoThreshold;
    }
}
