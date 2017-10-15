package com.gmail.btheo95.musicflashlight.util;

import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

/**
 * Created by btheo on 10/16/2017.
 */

public class FireBaseLogs {

    public static void menuClick(FirebaseAnalytics firebaseAnalytics) {
        Bundle params = new Bundle();
        firebaseAnalytics.logEvent("menu_click", params);
    }

    public static void flashUsed(FirebaseAnalytics firebaseAnalytics, int radioChecked) {
        Bundle params = new Bundle();
        int flashMode = FlashMode.getFlashModeByCheckedRadio(radioChecked);
        String modeText;
        switch (flashMode) {
            case FlashMode.TORCH:
                modeText = "torch";
                break;
            case FlashMode.MUSIC:
                modeText = "musical";
                break;
            case FlashMode.STROBE:
                modeText = "strobe";
                break;
            default:
                modeText = "bug";
        }
        params.putString("mode", modeText);
        firebaseAnalytics.logEvent("flash_used", params);
    }

    public static void aboutClick(FirebaseAnalytics firebaseAnalytics) {
        Bundle params = new Bundle();
        firebaseAnalytics.logEvent("about_click", params);
    }

    public static void licenseClick(FirebaseAnalytics firebaseAnalytics) {
        Bundle params = new Bundle();
        firebaseAnalytics.logEvent("license_click", params);
    }
}
