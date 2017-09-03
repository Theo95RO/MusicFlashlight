package com.gmail.btheo95.musicflashlight.runnable;

import android.util.Log;

import com.gmail.btheo95.musicflashlight.exception.CameraNotReachebleException;
import com.gmail.btheo95.musicflashlight.exception.FlashAlreadyInUseException;
import com.gmail.btheo95.musicflashlight.exception.FlashNotReachebleException;
import com.gmail.btheo95.musicflashlight.exception.MicNotReachebleException;

import java.util.concurrent.TimeUnit;

/**
 * Created by btheo on 3/16/2017.
 */

public final class MusicStrobe extends StrobeRunnable {

    private static final String TAG = MusicStrobe.class.getSimpleName();

    //    private static final double AMPLITUDE_CORRECTION_COEFFICIENT = 0.10;
    //    private static final int THREAD_WAITING_TIME = 25;
    public static final int MIN_AVERAGE_AMPLITUDE = 125;
    private static final float THRESHOLD_COEFFICIENT = 1.25f;
    private static final float AMPLITUDE_CORRECTION_COEFFICIENT = 0.15f;
    private static final int THREAD_WAITING_TIME = 50;
    public static final int MAX_AVERAGE_AMPLITUDE = (int) (32767 - (THRESHOLD_COEFFICIENT - 1) * 32767); // related to THRESHOLD_COEFFICIENT

    private float manualThresholdCoefficient = THRESHOLD_COEFFICIENT;
    private int manualMaxAverageAmplitude = MAX_AVERAGE_AMPLITUDE;
    private int mAverageAmplitude;
    private boolean mAutoThreshold;

    public MusicStrobe() {
        mAutoThreshold = true;
        mAverageAmplitude = 1000;
    }

    public MusicStrobe(boolean autoThreshold, int thresholdCoefficient) {
        mAutoThreshold = autoThreshold;
        mAverageAmplitude = 1000;
        setThresholdCoefficient(thresholdCoefficient);
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

                calculateAverageAmplitude(currentAmplitude);

                Log.v(TAG, "current amplitude: " + currentAmplitude);
                Log.v(TAG, "average amplitude: " + mAverageAmplitude);

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
            Log.v(TAG, "Threshold coefficient: " + manualThresholdCoefficient);
            float value = mAverageAmplitude * manualThresholdCoefficient;
            Log.v(TAG, "Avg: " + mAverageAmplitude + " Threshold: " + value);
            return value;
        }
    }

    private void calculateAverageAmplitude(int currentAmplitude) {
        mAverageAmplitude -= mAverageAmplitude * AMPLITUDE_CORRECTION_COEFFICIENT;
        mAverageAmplitude += currentAmplitude * AMPLITUDE_CORRECTION_COEFFICIENT;

        if (mAutoThreshold) {
            if (mAverageAmplitude > MAX_AVERAGE_AMPLITUDE) {
                mAverageAmplitude = MAX_AVERAGE_AMPLITUDE;
            } else if (mAverageAmplitude < MIN_AVERAGE_AMPLITUDE) {
                mAverageAmplitude = MIN_AVERAGE_AMPLITUDE;
            }
        } else {
            if (mAverageAmplitude > manualMaxAverageAmplitude) {
                mAverageAmplitude = manualMaxAverageAmplitude;
            } else if (mAverageAmplitude < MIN_AVERAGE_AMPLITUDE) {
                mAverageAmplitude = MIN_AVERAGE_AMPLITUDE;
            }
        }
    }

    public boolean isAutoThreshold() {
        return mAutoThreshold;
    }

    public synchronized void setAutoThreshold() {
        this.mAutoThreshold = true;
    }

    public synchronized void setManualThreshold(float thresholdCoefficient) {
        setThresholdCoefficient(thresholdCoefficient);
        this.mAutoThreshold = false;
    }

    public void setThresholdCoefficient(float thresholdCoefficient) {
        float value = (thresholdCoefficient + 100) / 100;
        Log.d(TAG, "setThresholdCoefficient() " + thresholdCoefficient + " " + value + " " + mAutoThreshold);
        this.manualThresholdCoefficient = value;
        this.manualMaxAverageAmplitude = (int) (32767 - (this.manualThresholdCoefficient - 1) * 32767);
    }
}
