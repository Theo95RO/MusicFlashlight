package com.gmail.btheo95.musicflashlight.resource;

import android.media.MediaRecorder;

import java.io.IOException;

/**
 * Created by btheo on 23/02/2016.
 */

public class SoundMeter {

    private MediaRecorder mRecorder = null;
//    private boolean mIsStarted = false;

    public void start() throws IOException {
        if (mRecorder == null) {
            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mRecorder.setOutputFile("/dev/null");
            mRecorder.prepare();
            mRecorder.start();
//            mIsStarted = true;
        }
    }

    public void stop() {
        if (mRecorder != null
//                && mIsStarted
                ) {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
//            mIsStarted = false;
        }
    }

    public double getAmplitude() {
        if (mRecorder != null)
            return mRecorder.getMaxAmplitude();
        else
            return 0;
    }

}
