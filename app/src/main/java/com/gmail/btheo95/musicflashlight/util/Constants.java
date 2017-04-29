package com.gmail.btheo95.musicflashlight.util;

import com.gmail.btheo95.musicflashlight.R;

/**
 * Created by btheo on 4/6/2017.
 */

public class Constants {
    public static final int STROBE_SEEK_BAR_MIN = 5;
    public static final int STROBE_SEEK_BAR_MAX = 800 - STROBE_SEEK_BAR_MIN;
    public static final int STROBE_SEEK_BAR_DEFAULT_VALUE = STROBE_SEEK_BAR_MAX / 2;

    public static final String PREFERENCE_RUN_IN_BACKGROUND_KEY = "preference_run_in_background";
    public static final boolean PREFERENCE_RUN_IN_BACKGROUND_DEFAULT = false;

    public static final String PREFERENCE_FLASH_MODE_KEY = "preference_flash_mode";
    public static final int PREFERENCE_FLASH_MODE_DEFAULT = R.id.radio_mode_torch;
}
