package com.gmail.btheo95.musicflashlight.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.gmail.btheo95.musicflashlight.R;
import com.gmail.btheo95.musicflashlight.activity.MainActivity;
import com.gmail.btheo95.musicflashlight.exception.CameraNotReachebleException;
import com.gmail.btheo95.musicflashlight.exception.FlashAlreadyInUseException;
import com.gmail.btheo95.musicflashlight.exception.FlashNotReachebleException;
import com.gmail.btheo95.musicflashlight.exception.MicNotReachebleException;
import com.gmail.btheo95.musicflashlight.runnable.ClassicStrobe;
import com.gmail.btheo95.musicflashlight.runnable.MusicStrobe;
import com.gmail.btheo95.musicflashlight.runnable.StrobeRunnable;

public class FlashlightIntentService extends IntentService {

    private static final String TAG = FlashlightIntentService.class.getSimpleName();
    private static final int NOTIFICATION_ID = 1337;

    public static final String ACTION_MUSIC_FLASHLIGHT = "com.gmail.btheo95.musicflashlight.service.action.MUSIC";
    public static final String ACTION_STROBE_FLASHLIGHT = "com.gmail.btheo95.musicflashlight.service.action.STROBE";

    public static final String EXTRA_FREQUENCY = "extra_frequency";

    public static final int DEFAULT_FREQUENCY = 50;

    private final IBinder mBinder = new LocalBinder();
    private StrobeRunnable mStrobe;

    public FlashlightIntentService() {
        super("FlashlightIntentService");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent == null) {
            return;
        }

        startForeground(NOTIFICATION_ID, buildForegroundNotification());

        handleAction(intent);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy()");
        stopForeground(true);
        super.onDestroy();
    }

    private Notification buildForegroundNotification() {
        Log.d(TAG, "buildForegroundNotification()");

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        return new NotificationCompat.Builder(this)
                .setContentTitle(getString(R.string.notification_flash_title))
                .setContentText(getString(R.string.notification_flash_content))
                .setSmallIcon(R.drawable.ic_flash_on_white_24dp)
                .setOngoing(true)
                .setContentIntent(pendingIntent)
                .build();
    }

    public static Intent createIntentForActionMusical(Context context) {
        Intent intent = new Intent(context, FlashlightIntentService.class);
        intent.setAction(ACTION_MUSIC_FLASHLIGHT);
        return intent;
    }

    public static Intent createIntentForActionStrobe(Context context, int frequency) {
        Intent intent = new Intent(context, FlashlightIntentService.class);
        intent.setAction(ACTION_STROBE_FLASHLIGHT);
        intent.putExtra(EXTRA_FREQUENCY, frequency);
        return intent;
    }

    public static Intent bindAndStart(Context context, ServiceConnection serviceConnection, Intent intent) {
        start(context, intent);
        bind(context, serviceConnection, intent);
        return intent;
    }

    public static Intent start(Context context, Intent intent) {
        context.startService(intent);
        return intent;
    }

    public static void bind(Context context, ServiceConnection serviceConnection, Intent intent) {
        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    public void unbindAndStop(final Context context, final ServiceConnection serviceConnection, boolean shouldCloseResources) {

        if (shouldCloseResources) {
            mStrobe.setOnStopListener(new StrobeRunnable.OnStopListener() {
                @Override
                public void onStop() {
                    Log.d(TAG, "started unbinding");
                    unbind(context, serviceConnection);
                    Log.d(TAG, "unbind finished");
                }
            });
            Log.d(TAG, "started stopping resource");
        } else {
            unbind(context, serviceConnection);
        }
        stop(shouldCloseResources);

    }

    public static void unbind(Context context, ServiceConnection serviceConnection) {
        context.unbindService(serviceConnection);
    }

    public void stop(boolean shouldCloseResources) {
        if (mStrobe != null) {
            mStrobe.shutdown(shouldCloseResources);
        }
    }

    public void changeAction(Context context, ServiceConnection serviceConnection, Intent intent) {
        unbindAndStop(context, serviceConnection, false);
        bindAndStart(context, serviceConnection, intent);
    }

    private void handleActionMusicFlashlight() {
        mStrobe = new MusicStrobe();
        startStrobe();
    }

    private void handleActionStrobeFlashlight(int frequency) {
        mStrobe = new ClassicStrobe(frequency);
        startStrobe();
    }

    private void startStrobe() {
        try {
            mStrobe.run();
        } catch (FlashAlreadyInUseException e) {
            broadcastCameraAlreadyInUse();
            mStrobe.shutdown();
        } catch (FlashNotReachebleException e) {
            broadcastFlashNotReacheble();
            mStrobe.shutdown();
        } catch (CameraNotReachebleException e) {
            broadcastCameraNotReacheble();
            mStrobe.shutdown();
        } catch (MicNotReachebleException e) {
            broadcastMicNotReacheble();
            mStrobe.shutdown();
        }
    }

    private void broadcastMicNotReacheble() {
        broadcastToMainActivity(MainActivity.INTENT_FILTER_MESSAGE_NO_MIC);
    }

    private void broadcastCameraNotReacheble() {
        broadcastToMainActivity(MainActivity.INTENT_FILTER_MESSAGE_NO_CAMERA);
    }

    private void broadcastFlashNotReacheble() {
        broadcastToMainActivity(MainActivity.INTENT_FILTER_MESSAGE_NO_FLASH);
    }

    private void broadcastCameraAlreadyInUse() {
        broadcastToMainActivity(MainActivity.INTENT_FILTER_MESSAGE_CAMERA_IN_USE);
    }

    private void broadcastToMainActivity(String message) {
        Intent intent = new Intent(MainActivity.INTENT_FILTER);
        intent.putExtra(MainActivity.INTENT_FILTER_MESSAGE_KEY, message);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

    }

    private void handleAction(Intent intent) {
        final String action = intent.getAction();

        switch (action) {
            case ACTION_MUSIC_FLASHLIGHT:
                handleActionMusicFlashlight();
                break;
            case ACTION_STROBE_FLASHLIGHT:
                int frequency = intent.getExtras().getInt(EXTRA_FREQUENCY, DEFAULT_FREQUENCY);
                handleActionStrobeFlashlight(frequency);
                break;

            default:
                break;
        }
    }

    public void setStrobeFrequency(int frequency) {
        if (mStrobe instanceof ClassicStrobe) {
            ((ClassicStrobe) mStrobe).setFrequency(frequency);
        }
    }

    public class LocalBinder extends Binder {
        public FlashlightIntentService getService() {
            return FlashlightIntentService.this;
        }
    }
}
