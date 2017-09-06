package com.gmail.btheo95.musicflashlight.runnable;

import com.gmail.btheo95.musicflashlight.exception.CameraNotReachableException;
import com.gmail.btheo95.musicflashlight.exception.FlashAlreadyInUseException;
import com.gmail.btheo95.musicflashlight.exception.FlashNotReachableException;
import com.gmail.btheo95.musicflashlight.exception.MicNotReachableException;

import java.util.concurrent.TimeUnit;

/**
 * Created by btheo on 4/27/2017.
 */

public class Torch extends StrobeRunnable {

    private static final String TAG = Torch.class.getSimpleName();

    @Override
    public void onStart() throws FlashAlreadyInUseException, CameraNotReachableException, FlashNotReachableException, MicNotReachableException {
        try {
            //hack like so the service doesn't die
            while (!mIsRunnableShutdown.get()) {
//                if (!mFlashIsOn) {
                turnFlashOn();
//                }
                TimeUnit.MILLISECONDS.sleep(100);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
