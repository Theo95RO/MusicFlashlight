package com.gmail.btheo95.musicflashlight.runnable;

import android.util.Log;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Created by btheo on 3/16/2017.
 */

public final class MusicStrobe extends StrobeRunnable {

    private static final String TAG = MusicStrobe.class.getSimpleName();

    private static final double AMPLITUDE_CORRECTION_COEFFICIENT = 0.15;
    //    private static final double AMPLITUDE_CORRECTION_COEFFICIENT = 0.10;
    private static final double THRESHOLD_COEFFICIENT = 1.25;
    private static final int THREAD_WAITING_TIME = 50;
//    private static final int THREAD_WAITING_TIME = 25;

    private double mAverageAmplitude;
    private boolean mAutoThreshold;

    public MusicStrobe() {
        mAutoThreshold = true;
        mAverageAmplitude = 100;
    }

    public MusicStrobe(boolean autoThreshold, double initialAmplitude) {
        mAutoThreshold = autoThreshold;
        mAverageAmplitude = initialAmplitude;
    }

    @Override
    public void run() {
        Log.d(TAG, "run()");
        try {
            while (!mIsRunnableShutdown) {
                startResourcesIfNotStarted();
                double currentAmplitude = getMicrophoneAmplitude();
                if (currentAmplitude > mAverageAmplitude * THRESHOLD_COEFFICIENT) {
                    turnFlashOn();
                } else {
                    turnFlashOff();
                }
                if (mAutoThreshold) {
                    calculateAutoThreshold(currentAmplitude);
                }
                Log.v(TAG, "current amplitude: " + currentAmplitude);
                Log.v(TAG, "threshold: " + mAverageAmplitude + " - " + Thread.currentThread().getName());

                TimeUnit.MILLISECONDS.sleep(THREAD_WAITING_TIME);
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            Log.d(TAG, "Stopping Strobe: " + Thread.currentThread().getName());
            turnFlashOff();
            if (mShouldCloseResources) {
                stopResources();
//                notifyListener();
            }
        }
    }

    private void calculateAutoThreshold(double currentAmplitude) {
        mAverageAmplitude -= mAverageAmplitude * AMPLITUDE_CORRECTION_COEFFICIENT;
        mAverageAmplitude += currentAmplitude * AMPLITUDE_CORRECTION_COEFFICIENT;

//        double correction = currentAmplitude * AMPLITUDE_CORRECTION_COEFFICIENT;
//        if (currentAmplitude > mAverageAmplitude) {
//            mAverageAmplitude += correction;
//        } else {
//            mAverageAmplitude -= correction;
//        }
    }

    public double getThreshold() {
        return mAverageAmplitude;
    }

    public void setThreshold(double threshold) {
        this.mAverageAmplitude = threshold;
    }

    public boolean isAutoThreshold() {
        return mAutoThreshold;
    }

    public void setAutoThreshold(boolean autoThreshold) {
        this.mAutoThreshold = autoThreshold;
    }
}
