package com.gmail.btheo95.musicflashlight.runnable;

import com.gmail.btheo95.musicflashlight.exception.CameraNotReachableException;
import com.gmail.btheo95.musicflashlight.exception.FlashAlreadyInUseException;
import com.gmail.btheo95.musicflashlight.exception.FlashNotReachableException;
import com.gmail.btheo95.musicflashlight.exception.MicNotReachableException;

import java.util.concurrent.TimeUnit;

/**
 * Created by btheo on 4/6/2017.
 */

public class ClassicStrobe extends StrobeRunnable {

    private static final String TAG = ClassicStrobe.class.getSimpleName();
    private int mFrequency;

    public ClassicStrobe(int frequency) {
        mFrequency = frequency;
    }

    @Override
    public void onStart() throws FlashAlreadyInUseException, CameraNotReachableException, FlashNotReachableException, MicNotReachableException {
        try {
            while (!mIsRunnableShutdown.get()) {
                toggleFlash();
                TimeUnit.MILLISECONDS.sleep(mFrequency);
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void setFrequency(int frequency) {
        mFrequency = frequency;
    }
}

