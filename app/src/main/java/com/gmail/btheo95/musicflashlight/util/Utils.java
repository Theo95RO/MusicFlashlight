package com.gmail.btheo95.musicflashlight.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.util.Log;
import android.view.WindowManager;

/**
 * Created by btheo on 4/5/2017.
 */

public class Utils {

    private static final String TAG = Utils.class.getSimpleName();

    public static boolean isMyServiceRunning(Class<?> serviceClass, Context context) {
        Log.d(TAG, "isMyServiceRunning()");
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.d(TAG, "isMyServiceRunning() -> true");

                return true;
            }
        }
        Log.d(TAG, "isMyServiceRunning() -> false");
        return false;
    }
}
