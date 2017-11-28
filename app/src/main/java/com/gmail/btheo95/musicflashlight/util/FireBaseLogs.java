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
        switch (flashMode) {
            case FlashMode.TORCH:
                firebaseAnalytics.logEvent("flash_torch_used", params);
                break;
            case FlashMode.MUSIC:
                firebaseAnalytics.logEvent("flash_musical_used", params);
                break;
            case FlashMode.STROBE:
                firebaseAnalytics.logEvent("flash_strobe_used", params);
                break;
            default:
                firebaseAnalytics.logEvent("bug_flash_mode", params);
        }
        firebaseAnalytics.logEvent("flash_used", params);
    }

    public static void aboutClick(FirebaseAnalytics firebaseAnalytics) {
        Bundle params = new Bundle();
        firebaseAnalytics.logEvent("about_click", params);
    }

    public static void menuLicenseClick(FirebaseAnalytics firebaseAnalytics) {
        licenseClick(firebaseAnalytics);
        Bundle params = new Bundle();
        firebaseAnalytics.logEvent("menu_license_click", params);
    }

    public static void aboutLicenseClick(FirebaseAnalytics firebaseAnalytics) {
        licenseClick(firebaseAnalytics);
        Bundle params = new Bundle();
        firebaseAnalytics.logEvent("about_license_click", params);
    }

    public static void licenseClick(FirebaseAnalytics firebaseAnalytics) {
        Bundle params = new Bundle();
        firebaseAnalytics.logEvent("license_click", params);
    }
}
