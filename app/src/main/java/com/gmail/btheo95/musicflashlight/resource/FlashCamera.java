package com.gmail.btheo95.musicflashlight.resource;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;

import com.gmail.btheo95.musicflashlight.exception.CameraNotReachableException;
import com.gmail.btheo95.musicflashlight.exception.FlashAlreadyInUseException;
import com.gmail.btheo95.musicflashlight.exception.FlashNotReachableException;

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

    public void startCamera() throws IOException, CameraNotReachableException, FlashNotReachableException {
        try {
            mCamera = Camera.open();
        } catch (RuntimeException ex) {
            throw new CameraNotReachableException();
        }
        if (mCamera == null) {
            throw new CameraNotReachableException();
        }
        mDummySurfaceTexture = new SurfaceTexture(0);
        mCamera.setPreviewTexture(mDummySurfaceTexture);

        try {
            mCamera.startPreview();
        } catch (RuntimeException ex) {
            throw new CameraNotReachableException();
        }

        try {
            mParametersFlashOn = mCamera.getParameters();
        } catch (RuntimeException ex) {
            mCamera.stopPreview();
            throw new FlashNotReachableException();
        }

        //depends on device
        List<String> flashModesList = mCamera.getParameters().getSupportedFlashModes();
        if (flashModesList == null) {
            throw new FlashNotReachableException();
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
