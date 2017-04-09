package com.gmail.btheo95.musicflashlight.resource;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;

import com.gmail.btheo95.musicflashlight.exception.CameraNotReachebleException;
import com.gmail.btheo95.musicflashlight.exception.FlashAlreadyInUseException;
import com.gmail.btheo95.musicflashlight.exception.FlashNotReachebleException;

import java.io.IOException;
import java.util.List;

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

    public void startCamera() throws IOException, CameraNotReachebleException, FlashNotReachebleException {
        mCamera = Camera.open();
        if (mCamera == null) {
            throw new CameraNotReachebleException();
        }
        mDummySurfaceTexture = new SurfaceTexture(0);
        mCamera.setPreviewTexture(mDummySurfaceTexture);
        mCamera.startPreview();

        mParametersFlashOn = mCamera.getParameters();
        //depends on device

        List<String> flashModesList = mCamera.getParameters().getSupportedFlashModes();
        if (flashModesList == null) {
            throw new FlashNotReachebleException();
        }
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
        try {
            turnFlashOff();
        } catch (FlashAlreadyInUseException e) {
            e.printStackTrace();
        }
        mCamera.stopPreview();
        mCamera.release();
    }

    public void turnFlashOn() throws FlashAlreadyInUseException {
        if (null == mCamera) {
            return;
        }
        if (!mFlashIsOn) {
            setParameters(mParametersFlashOn);
            mFlashIsOn = true;
        }
    }

    public void turnFlashOff() throws FlashAlreadyInUseException {
        if (null == mCamera) {
            return;
        }
        if (mFlashIsOn) {
            setParameters(mParametersFlashOff);
            mFlashIsOn = false;
        }
    }

    public void toggleFlash() throws FlashAlreadyInUseException {
        if (mFlashIsOn) {
            turnFlashOff();
        } else {
            turnFlashOn();
        }
    }

    public boolean isFlashOn() {
        return mFlashIsOn;
    }

    private void setParameters(Camera.Parameters parameters) throws FlashAlreadyInUseException {
        try {
            mCamera.setParameters(parameters);
        } catch (RuntimeException ex) {
            throw new FlashAlreadyInUseException();
        }
    }
}
