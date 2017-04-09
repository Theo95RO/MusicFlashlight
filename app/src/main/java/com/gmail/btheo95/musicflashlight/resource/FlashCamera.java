package com.gmail.btheo95.musicflashlight.resource;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by btheo on 3/16/2017.
 */

@SuppressWarnings("deprecation")
public class FlashCamera {

    private static final String TAG = FlashCamera.class.getSimpleName();

    private Camera mCamera = null;
    @SuppressWarnings("FieldCanBeLocal") // it is global so it doesn't get cleaned by the GC
    private SurfaceTexture mDummySurfaceTexture;
    private Camera.Parameters mParametersFlashOn;
    private Camera.Parameters mParametersFlashOff;
    private boolean mFlashIsOn = false;

    public FlashCamera() {
    }

    public static boolean deviceHasFlashlight() {
        Camera camera = Camera.open();
        boolean hasFlash = (camera.getParameters().getSupportedFlashModes() != null);
        camera.release();
        return hasFlash;
    }

    public static boolean deviceHasCamera() {
        Camera camera = Camera.open();
        if (camera == null) {
            return false;
        } else {
            camera.release();
            return true;
        }
    }

    public static boolean deviceFlashIsReachenle() {
        return deviceHasCamera() && deviceHasFlashlight();
    }

    public void startCamera() throws IOException {
        mCamera = Camera.open();
        mDummySurfaceTexture = new SurfaceTexture(0);
        mCamera.setPreviewTexture(mDummySurfaceTexture);
        mCamera.startPreview();

        mParametersFlashOn = mCamera.getParameters();
        //depends on device
        ArrayList<String> flashModesList = new ArrayList<>(mCamera.getParameters().getSupportedFlashModes());
        if (flashModesList.contains(Camera.Parameters.FLASH_MODE_TORCH)) {
            mParametersFlashOn.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        } else if (flashModesList.contains(Camera.Parameters.FLASH_MODE_ON)) {
            mParametersFlashOn.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
        } else if (flashModesList.contains(Camera.Parameters.FLASH_MODE_RED_EYE)) {
            mParametersFlashOn.setFlashMode(Camera.Parameters.FLASH_MODE_RED_EYE);
        }

        mParametersFlashOff = mCamera.getParameters();
        mParametersFlashOff.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
    }

    public void stopCamera() {
        if (null == mCamera) {
            return;
        }
        turnFlashOff();
        mCamera.stopPreview();
        mCamera.release();
    }

    public void turnFlashOn() {
        if (null == mCamera) {
            return;
        }
        if (!mFlashIsOn) {
            mCamera.setParameters(mParametersFlashOn);
            mFlashIsOn = true;
        }
    }

    public void turnFlashOff() {
        if (null == mCamera) {
            return;
        }
        if (mFlashIsOn) {
            mCamera.setParameters(mParametersFlashOff);
            mFlashIsOn = false;
        }
    }

    public void toggleFlash() {
        if (mFlashIsOn) {
            turnFlashOff();
        } else {
            turnFlashOn();
        }
    }

    public boolean isFlashOn() {
        return mFlashIsOn;
    }
}
