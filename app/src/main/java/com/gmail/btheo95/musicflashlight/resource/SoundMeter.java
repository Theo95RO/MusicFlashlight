package com.gmail.btheo95.musicflashlight.resource;

import android.media.MediaRecorder;

import com.gmail.btheo95.musicflashlight.exception.MicNotReachableException;

import java.io.IOException;

/**
 * Created by btheo on 23/02/2016.
 */

public class SoundMeter {

    private MediaRecorder mRecorder = null;

    public void start() throws IOException, MicNotReachableException {
        if (mRecorder == null) {
            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mRecorder.setOutputFile("/dev/null");
            mRecorder.prepare();
            try {
                mRecorder.start();
            } catch (RuntimeException ex) {
                throw new MicNotReachableException();
            }
        }
    }

    public void stop() {
        if (mRecorder != null) {
            try {
                mRecorder.stop();
                mRecorder.release();
            } catch (RuntimeException ex) {
                ex.printStackTrace();
            }
            mRecorder = null;
        }
    }

    public int getAmplitude() {
        if (mRecorder != null)
            return mRecorder.getMaxAmplitude();
        else
            return 0;
    }

}
