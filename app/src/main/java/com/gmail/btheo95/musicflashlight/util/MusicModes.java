package com.gmail.btheo95.musicflashlight.util;

import com.gmail.btheo95.musicflashlight.R;

/**
 * Created by btheo on 9/27/2017.
 */

public class MusicModes {
    public final static int AUTO = 0;
    public final static int MANUAL = 1;

    static public int getMusicModeByCheckedRadio(int id) {
        switch (id) {
            case R.id.radio_musical_sensibility_auto:
                return AUTO;
            case R.id.radio_musical_sensibility_manual:
                return MANUAL;
            default:
                return AUTO;
        }
    }

    static public int getCheckedRadioBMusicMode(int mode) {
        switch (mode) {
            case AUTO:
                return R.id.radio_musical_sensibility_auto;
            case MANUAL:
                return R.id.radio_musical_sensibility_manual;
            default:
                return R.id.radio_musical_sensibility_auto;
        }
    }

}
