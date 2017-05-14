package com.gmail.btheo95.musicflashlight.runnable;

import com.gmail.btheo95.musicflashlight.exception.CameraNotReachebleException;
import com.gmail.btheo95.musicflashlight.exception.FlashAlreadyInUseException;
import com.gmail.btheo95.musicflashlight.exception.FlashNotReachebleException;
import com.gmail.btheo95.musicflashlight.exception.MicNotReachebleException;

import java.util.concurrent.TimeUnit;

/**
 * Created by btheo on 4/27/2017.
 */

public class Torch extends StrobeRunnable {

    private static final String TAG = Torch.class.getSimpleName();

    @Override
    public void onStart() throws FlashAlreadyInUseException, CameraNotReachebleException, FlashNotReachebleException, MicNotReachebleException {
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

//    @Override
//    protected void onPostStart() throws FlashAlreadyInUseException {
//    }
//
//    @Override
//    public void shutdown() {
//        try {
//            super.onPostStart();
//        } catch (FlashAlreadyInUseException e) {
//            Log.e(TAG, "Flash already in use. Cannot turn it off");
//        }
//    }
}
