package com.gmail.btheo95.musicflashlight.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.util.Log;
import android.view.WindowManager;

import com.gmail.btheo95.musicflashlight.service.FlashlightIntentService;

/**
 * Created by btheo on 4/5/2017.
 */

public class Utils {

    private static final String TAG = Utils.class.getSimpleName();

    public static boolean isMyServiceRunning(Class<?> serviceClass, Context context) {
        Log.d(TAG, "isMyServiceRunning()");
        return FlashlightIntentService.isServiceRunning;
//        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//        // TODO: Update for Android O
//        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
//            if (serviceClass.getName().equals(service.service.getClassName())) {
//                Log.d(TAG, "isMyServiceRunning() -> true");
//
//                return true;
//            }
//        }
//        Log.d(TAG, "isMyServiceRunning() -> false");
//        return false;
    }

    public static void preventScreenSleeping(Activity activity) {
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    public static void allowScreenSleeping(Activity activity) {
        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

}
