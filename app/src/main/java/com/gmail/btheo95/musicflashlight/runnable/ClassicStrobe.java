package com.gmail.btheo95.musicflashlight.runnable;

import com.gmail.btheo95.musicflashlight.exception.CameraNotReachebleException;
import com.gmail.btheo95.musicflashlight.exception.FlashAlreadyInUseException;
import com.gmail.btheo95.musicflashlight.exception.FlashNotReachebleException;
import com.gmail.btheo95.musicflashlight.exception.MicNotReachebleException;

import java.io.IOException;
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
    public void run() throws FlashAlreadyInUseException, CameraNotReachebleException, FlashNotReachebleException, MicNotReachebleException {
        try {
            while (!mIsRunnableShutdown) {
                startResourcesIfNotStarted();
                toggleFlash();
//                Thread.currentThread().wait(mFrequency);
                TimeUnit.MILLISECONDS.sleep(mFrequency);
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            turnFlashOff();
            if (mShouldCloseResources) {
                stopResources();
//                notifyListener();
            }
        }
    }

    public void setFrequency(int frequency) {
        mFrequency = frequency;
    }
}

