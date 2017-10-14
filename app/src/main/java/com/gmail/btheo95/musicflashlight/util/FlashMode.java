package com.gmail.btheo95.musicflashlight.util;

import com.gmail.btheo95.musicflashlight.R;

/**
 * Created by btheo on 9/27/2017.
 */

public class FlashMode {
    public final static int TORCH = 0;
    public final static int STROBE = 1;
    public final static int MUSIC = 2;

    static public int getFlashModeByCheckedRadio(int id) {
        switch (id) {
            case R.id.radio_mode_torch:
                return TORCH;
            case R.id.radio_mode_musical:
                return MUSIC;
            case R.id.radio_mode_strobe:
                return STROBE;
            default:
                return TORCH;

        }
    }

    static public int getCheckedRadioByFlashMode(int mode) {
        switch (mode) {
            case TORCH:
                return R.id.radio_mode_torch;
            case MUSIC:
                return R.id.radio_mode_musical;
            case STROBE:
                return R.id.radio_mode_strobe;
            default:
                return R.id.radio_mode_torch;
        }
    }
}
