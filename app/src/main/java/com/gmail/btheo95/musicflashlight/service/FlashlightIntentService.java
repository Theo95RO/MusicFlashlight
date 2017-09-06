package com.gmail.btheo95.musicflashlight.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.gmail.btheo95.musicflashlight.R;
import com.gmail.btheo95.musicflashlight.activity.MainActivity;
import com.gmail.btheo95.musicflashlight.exception.CameraNotReachableException;
import com.gmail.btheo95.musicflashlight.exception.FlashAlreadyInUseException;
import com.gmail.btheo95.musicflashlight.exception.FlashNotReachableException;
import com.gmail.btheo95.musicflashlight.exception.MicNotReachableException;
import com.gmail.btheo95.musicflashlight.runnable.ClassicStrobe;
import com.gmail.btheo95.musicflashlight.runnable.MusicStrobe;
import com.gmail.btheo95.musicflashlight.runnable.StrobeRunnable;
import com.gmail.btheo95.musicflashlight.runnable.Torch;
import com.gmail.btheo95.musicflashlight.util.Constants;

import java.io.IOException;

public class FlashlightIntentService extends IntentService {

    private static final String TAG = FlashlightIntentService.class.getSimpleName();
    private static final int NOTIFICATION_ID = 1337;

    public static final String ACTION_MANUAL_MUSIC_FLASHLIGHT = "com.gmail.btheo95.musicflashlight.service.action.MANUAL_MUSIC";
    public static final String ACTION_AUTO_MUSIC_FLASHLIGHT = "com.gmail.btheo95.musicflashlight.service.action.AUTO_MUSIC";
    public static final String ACTION_STROBE_FLASHLIGHT = "com.gmail.btheo95.musicflashlight.service.action.STROBE";
    private static final String ACTION_TORCH_FLASHLIGHT = "com.gmail.btheo95.musicflashlight.service.action.TORCH";

    public static final String EXTRA_FREQUENCY = "extra_frequency";
    public static final String EXTRA_SENSIBILITY = "extra_sensibility";
    public static final int DEFAULT_STROBE_FREQUENCY = Constants.STROBE_SEEK_BAR_DEFAULT_VALUE;
    public static final int DEFAULT_MANUAL_MUSIC_SENSIBILITY = Constants.MUSIC_SEEK_BAR_DEFAULT_VALUE;

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

//        startForeground(NOTIFICATION_ID, buildForegroundNotification());

        handleAction(intent);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy()");
        stopForeground(true);
        super.onDestroy();
    }

    private Notification buildForegroundNotification() {
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // TODO: Update for Android O
        return new NotificationCompat.Builder(this)
                .setContentTitle(getString(R.string.notification_flash_title))
                .setContentText(getString(R.string.notification_flash_content))
                .setSmallIcon(R.drawable.ic_filled_light_bulb_white_24dp)
                .setOngoing(true)
                .setContentIntent(pendingIntent)
                .build();
    }

    public static Intent createIntentForActionMusicalAuto(Context context) {
        Intent intent = new Intent(context, FlashlightIntentService.class);
        intent.setAction(ACTION_AUTO_MUSIC_FLASHLIGHT);
        return intent;
    }

    public static Intent createIntentForActionMusicalManual(Context context, int thresholdCoefficient) {
        Intent intent = new Intent(context, FlashlightIntentService.class);
        intent.setAction(ACTION_MANUAL_MUSIC_FLASHLIGHT);
        intent.putExtra(EXTRA_SENSIBILITY, thresholdCoefficient);
        return intent;
    }

    public static Intent createIntentForActionStrobe(Context context, int frequency) {
        Intent intent = new Intent(context, FlashlightIntentService.class);
        intent.setAction(ACTION_STROBE_FLASHLIGHT);
        intent.putExtra(EXTRA_FREQUENCY, frequency);
        return intent;
    }

    public static Intent createIntentForActionTorch(Context context) {
        Intent intent = new Intent(context, FlashlightIntentService.class);
        intent.setAction(ACTION_TORCH_FLASHLIGHT);
        return intent;
    }

    public static Intent startService(Context context, Intent intent) {
        Log.d(TAG, "Starting service");
        context.startService(intent);
        return intent;
    }

    public void stopService(boolean shouldTurnFlashOff, boolean shouldCloseResources) {

        if (mStrobe != null) {
            mStrobe.setShouldCloseResources(shouldCloseResources);
            mStrobe.setShouldTurnFlashOffAtShutDown(shouldTurnFlashOff);
            mStrobe.setOnStopListener(new StrobeRunnable.OnStopListener() {
                @Override
                public void onStop() {
                    stopSelf();
                }
            });

            mStrobe.shutdown();

        } else {
            Log.e(TAG, "strobe is null when trying to shutdown");
            stopSelf();
        }
    }

    public static void unbindService(Context context, ServiceConnection serviceConnection) {
        context.unbindService(serviceConnection);
    }

    public static void bindService(Context context, ServiceConnection serviceConnection, Intent intent) {
        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    public static Intent bindAndStartService(Context context, ServiceConnection serviceConnection, Intent intent) {
        startService(context, intent);
        bindService(context, serviceConnection, intent);
        return intent;
    }

    public void unbindAndStopService(final Context context, final ServiceConnection serviceConnection) {
        unbindAndStopService(context, serviceConnection, true, false);
//        stopService();
//        unbindService(context, serviceConnection);
    }

    public void unbindAndStopService(final Context context, final ServiceConnection serviceConnection, boolean shouldTurnFlashOff, boolean shouldCloseResources) {
        stopService(shouldTurnFlashOff, shouldCloseResources);
        unbindService(context, serviceConnection);
    }

    public void startForeground() {
        startForeground(NOTIFICATION_ID, buildForegroundNotification());
    }

    public void stopForeground() {
        stopForeground(true);
    }

    public void changeAction(final Intent intent) {

//        stopService(false);
//        startService(context, intent);

        Handler handler = new Handler(getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                mStrobe.setOnStopListener(new StrobeRunnable.OnStopListener() {
                    @Override
                    public void onStop() {
                        handleAction(intent);
                    }
                });

                mStrobe.shutdown();
            }
        });

//        unbindAndStopService(context, serviceConnection, false, false);
//        bindAndStartService(context, serviceConnection, intent);
    }

    private void handleActionAutoMusicFlashlight() {
        mStrobe = new MusicStrobe();
        startStrobe();
    }

    private void handleActionManualMusicFlashlight(int thresholdCoefficient) {
        mStrobe = new MusicStrobe(false, thresholdCoefficient);
        startStrobe();
    }

    private void handleActionStrobeFlashlight(int frequency) {
        mStrobe = new ClassicStrobe(frequency);
        startStrobe();
    }


    private void handleActionTorchFlashlight() {
        mStrobe = new Torch();
        startStrobe();
    }

    private void startStrobe() {
        try {
            mStrobe.start();
        } catch (FlashAlreadyInUseException e) {
            broadcastCameraAlreadyInUse();
            mStrobe.shutdown();
        } catch (FlashNotReachableException e) {
            broadcastFlashNotReacheble();
            mStrobe.shutdown();
        } catch (CameraNotReachableException | IOException e) {
            broadcastCameraNotReacheble();
            mStrobe.shutdown();
        } catch (MicNotReachableException e) {
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
            case ACTION_AUTO_MUSIC_FLASHLIGHT:
                handleActionAutoMusicFlashlight();
                break;
            case ACTION_MANUAL_MUSIC_FLASHLIGHT:
                int thresholdCoefficient = intent.getExtras().getInt(EXTRA_SENSIBILITY, DEFAULT_MANUAL_MUSIC_SENSIBILITY);
                handleActionManualMusicFlashlight(thresholdCoefficient);
                break;
            case ACTION_STROBE_FLASHLIGHT:
                int frequency = intent.getExtras().getInt(EXTRA_FREQUENCY, DEFAULT_STROBE_FREQUENCY);
                handleActionStrobeFlashlight(frequency);
                break;
            case ACTION_TORCH_FLASHLIGHT:
                handleActionTorchFlashlight();
                break;
            default:
                Log.e(TAG, "Starting an action that does not exist");
                break;
        }
    }

    public void setStrobeFrequency(int frequency) {
        if (mStrobe instanceof ClassicStrobe) {
            ((ClassicStrobe) mStrobe).setFrequency(frequency);
        }
    }

    public void setMusicSensibility(int sensibility) {
        if (mStrobe instanceof MusicStrobe) {
            ((MusicStrobe) mStrobe).setThreshold(sensibility);
        }
    }

    public void setMusicModeAuto() {
        if (mStrobe instanceof MusicStrobe) {
            Log.d(TAG, "setMusicModeAuto()");
            ((MusicStrobe) mStrobe).setAutoThreshold();
        }
    }

    public void setMusicModeManual(int threshold) {
        if (mStrobe instanceof MusicStrobe) {
            Log.d(TAG, "setMusicModeManual()");
            ((MusicStrobe) mStrobe).setManualThreshold(threshold);
        }
    }

    public class LocalBinder extends Binder {
        public FlashlightIntentService getService() {
            return FlashlightIntentService.this;
        }
    }
}
