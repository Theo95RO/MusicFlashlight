package com.gmail.btheo95.musicflashlight.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * Created by btheo on 2/18/2017.
 */

public class Permissions {

    public final static String[] PERMISSIONS = new String[]{
            Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO};

    public static boolean arePermissionsGranted(Context context) {
        for (String permission : PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(context, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }

        return true;
    }

    public static boolean shouldShowRequestPermissionsRationale(Activity activity) {

        for (String permission : PERMISSIONS) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                return true;
            }
        }
        return false;
    }

    public static void requestPermissions(Activity activity, int requestCode) {
        if (!arePermissionsGranted(activity)) {
            ActivityCompat.requestPermissions(activity, PERMISSIONS, requestCode);
        }
    }

}
