package com.gmail.btheo95.musicflashlight.activity;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.RadioGroup;
import android.widget.SeekBar;

import com.gmail.btheo95.musicflashlight.R;
import com.gmail.btheo95.musicflashlight.fragment.AboutFragment;
import com.gmail.btheo95.musicflashlight.fragment.LicenseDialogFragment;
import com.gmail.btheo95.musicflashlight.fragment.MainContentFragment;
import com.gmail.btheo95.musicflashlight.resource.Strobe;
import com.gmail.btheo95.musicflashlight.service.FlashlightIntentService;
import com.gmail.btheo95.musicflashlight.util.Permissions;
import com.gmail.btheo95.musicflashlight.util.Utils;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements AboutFragment.OnFragmentInteractionListener, MainContentFragment.OnFragmentInteractionListener {

    private FirebaseAnalytics mFirebaseAnalytics;

    private static final String TAG = MainActivity.class.getName();
    private static final int PERMISSIONS_REQUEST_CODE = 0;
    private static final String STATE_FLASH = "state_flash";
    private static final String STATE_FRAGMENT = "state_fragment";
    private static final String STATE_SERVICE_BOUND = "state_service_bound";
    private static final String STATE_SERVICE_INTENT = "state_service_intent";

//    private static final String STATE_SHOULD_LOAD_AD = "state_should_load_ad";
//    private static final String STATE_FLASH_AVAILABILITY = "state_flash_availability";
//    private static final String STATE_MUSIC_STROBE_THRESHOLD = "state_music_strobe_threshold";

    private FloatingActionButton mFab;
    private AdRequest mAdRequest;
    private AdView mAdView;

    private FlashlightIntentService mFlashlightService;
    private Intent mFlashlightServiceIntent;
    private boolean mFlashServiceIsBound = false;

    private boolean mFlashIsOn = false;
    private int mCurrentFragmentId = -1;
    private MainContentFragment mMainContentFragment;

//    private boolean mShouldLoadAdd = false;
//    private boolean mFlashAvailabilityWasChecked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
//        mFlashIsReachable = (FlashCamera.deviceHasCamera() && FlashCamera.deviceHasFlashlight());
        initialiseViews();
//        startResources();
        mMainContentFragment = (MainContentFragment)
                getFragmentManager().findFragmentById(R.id.main_fragment);
    }

    private void startResources() {
        if (Permissions.arePermissionsGranted(this)) {
            try {
                Strobe.getInstance().start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

//            mAdView.setVisibility(View.GONE);
//            mFab.setEnabled(false);
//    setFragment(R.id.content_fragment_container, WarningFragment.newInstance(), null, null);

    private void initialiseViews() {

        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mAdView = (AdView) findViewById(R.id.ad_view);


        mAdRequest = new AdRequest.Builder()
                .addTestDevice("09CF6E3DB88CBC82AB6FDE98BE527965") // mine
                .addTestDevice("CD6899493C29DB07F6BAA044F3576813") // htc
                .addTestDevice("B0E733267B278B1A17AFEF83AD4D0984") // man
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();

        mAdView.setAlpha(0f);
        mAdView.setScaleX(0.9f);
        mAdView.setScaleY(0.9f);
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLeftApplication() {
                if (mFlashIsOn) {
                    fabAction(false);
                }
                super.onAdLeftApplication();
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                mAdView.animate()
                        .setDuration(getResources().getInteger(android.R.integer.config_shortAnimTime))
                        .alpha(1f)
                        .scaleY(1f)
                        .scaleX(1f)
                        .start();
            }
        });

        mFab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                fabAction(true);
            }
        });

        loadAd();
        changeFabIcon();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "onSaveInstanceState()");
        outState.putBoolean(STATE_FLASH, mFlashIsOn);
        outState.putInt(STATE_FRAGMENT, mCurrentFragmentId);
        outState.putBoolean(STATE_SERVICE_BOUND, mFlashServiceIsBound);
        outState.putParcelable(STATE_SERVICE_INTENT, mFlashlightServiceIntent);
//        outState.putBoolean(STATE_SHOULD_LOAD_AD, mShouldLoadAdd);
//        outState.putBoolean(STATE_FLASH_AVAILABILITY, mFlashAvailabilityWasChecked);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.d(TAG, "onRestoreInstanceState()");
        mFlashIsOn = savedInstanceState.getBoolean(STATE_FLASH);
        mCurrentFragmentId = savedInstanceState.getInt(STATE_FRAGMENT);
        mFlashlightServiceIntent = savedInstanceState.getParcelable(STATE_SERVICE_INTENT);

        boolean serviceWasBound = savedInstanceState.getBoolean(STATE_SERVICE_BOUND);

        if (serviceWasBound && mFlashIsOn && Utils.isMyServiceRunning(FlashlightIntentService.class, getApplicationContext())) {
            FlashlightIntentService.bind(getApplicationContext(), mServiceConnection, mFlashlightServiceIntent);
            updateFabState();
        } else {
            mFlashIsOn = false;
            mFlashServiceIsBound = false;
        }

//        mShouldLoadAdd = savedInstanceState.getBoolean(STATE_SHOULD_LOAD_AD);
//        mFlashAvailabilityWasChecked = savedInstanceState.getBoolean(STATE_FLASH_AVAILABILITY);

//        updateAdState();
        updateFragmentState();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()");

        if (mAdView != null) {
                mAdView.resume();
        }
//        startResources();
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause()");

        if (mAdView != null) {
            mAdView.pause();
        }

        if (!mFlashIsOn && !isChangingConfigurations()) {
            Strobe.getInstance().stop();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy()");

        if (mAdView != null) {
            mAdView.destroy();
        }

        if (mFlashServiceIsBound) {
            FlashlightIntentService.unbind(getApplicationContext(), mServiceConnection);
        }

        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        Log.d(TAG, "onRequestPermissionsResult()");

        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    fabAction(true);
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_main_about:
                displayAboutFragment();
                return true;
            case R.id.menu_main_licenses:
                displayLicensesDialogFragment();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if (mCurrentFragmentId != -1) {
            removeFragment(R.id.overlay_fragment_container, R.animator.fade_in_stop, R.animator.fade_out_stop);
            mCurrentFragmentId = -1;
            mFab.show();
            loadAd();
        } else {
            super.onBackPressed();
        }
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {

            Log.d(TAG, "onServiceConnected()");
            FlashlightIntentService.LocalBinder binder = (FlashlightIntentService.LocalBinder) service;
            mFlashlightService = binder.getService();
            mFlashServiceIsBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            Log.d(TAG, "onServiceDisconnected()");
            mFlashServiceIsBound = false;
        }
    };

    private void loadAd() {
//        if (!mShouldLoadAdd) {
//            return;
//        }
//        mAdView.setAlpha(1f);
//        mAdView.setScaleX(1f);
//        mAdView.setScaleY(1f);
        mAdView.loadAd(mAdRequest);
        mAdView.setVisibility(View.VISIBLE);
    }

    private void destroyAd() {
//        AlphaAnimation animation = new AlphaAnimation(1f, 0f);
//        animation.setDuration(android.R.integer.config_longAnimTime);
//        animation.setFillAfter(true);
//        mAdView.startAnimation(animation);
//        mAdView.animate()
//                .alpha(0f)
//                .scaleX(0.5f)
//                .scaleY(0.5f)
//                .setDuration(300)
//                .setListener(new AnimatorListenerAdapter() {
//                    @Override
//                    public void onAnimationEnd(Animator animation) {
//                        mAdView.destroy();
//                    }
//                })
//                .start();

        mAdView.destroy();

//        mAdView.setVisibility(View.INVISIBLE);
    }

    private void fabAction(boolean shouldVibrate) {
        if (!Permissions.arePermissionsGranted(MainActivity.this)) {
            if (Permissions.shouldShowRequestPermissionsRationale(MainActivity.this)) {
                showPermissionsRationale();
            } else {
                Permissions.requestPermissions(MainActivity.this, PERMISSIONS_REQUEST_CODE);
            }
            return;
        }
        mFlashIsOn = !mFlashIsOn;

        if (shouldVibrate) {
            vibrateFab();
        }
        animateFab();
        changeFabIcon();
//        if(!mFlashAvailabilityWasChecked){
//            if (!FlashCamera.deviceFlashIsReachenle()) {
//                displayFlashNotReacheble();
//                return;
//            }
//            mFlashAvailabilityWasChecked = true;
//        }
//        if (!mShouldLoadAdd) {
//            mShouldLoadAdd = true;
//            loadAd();
//        }

        if (!mFlashIsOn) {
            stopFlashlight();
        } else {
            startFlashlight();
        }
    }

    private void startFlashlight() {
        int checkedRadioId = getCheckedRadioId();
        mFlashlightServiceIntent = getIntentForServiceByCheckedRadioId(checkedRadioId);
        FlashlightIntentService.bindAndStart(getApplicationContext(), mServiceConnection, mFlashlightServiceIntent);

//            mFlashService = FlashlightIntentService.startActionMusicFlashlight(getApplicationContext());
//            startHeadlessFragment(stateLoss);
    }

    private void stopFlashlight() {
        mFlashlightService.unbindAndStop(getApplicationContext(), mServiceConnection, false);
        mFlashServiceIsBound = false;
//            stopHeadlessFragment();

    }

//    private void updateAdState(){
//        loadAd();
//    }

    private void updateFabState() {
        if (mFlashIsOn) {
            mFab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(MainActivity.this, R.color.primary)));
            changeFabIcon();
        } else if (mCurrentFragmentId != -1) {
            mFab.hide();
        }
    }

    private void updateFragmentState() {
        switch (mCurrentFragmentId) {
            case R.layout.fragment_about:
                displayAboutFragment();
        }
    }

    private void changeFabIcon() {
        if (mFlashIsOn) {
            mFab.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_flash_off_white_24dp));
        } else {
            mFab.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_flash_on_white_24dp));
        }
    }

    private void vibrateFab() {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(25);
    }

    private void animateFab() {
        int initialColor;
        int finalColor;
        if (!mFlashIsOn) {
            initialColor = ContextCompat.getColor(MainActivity.this, R.color.primary);
            finalColor = ContextCompat.getColor(MainActivity.this, R.color.accent);
        } else {
            initialColor = ContextCompat.getColor(MainActivity.this, R.color.accent);
            finalColor = ContextCompat.getColor(MainActivity.this, R.color.primary);
        }

        //could try ObjectAnimator
        final ValueAnimator animator = ObjectAnimator.ofInt(initialColor, finalColor);
        animator.setEvaluator(new ArgbEvaluator());
        animator.setInterpolator(new DecelerateInterpolator(1));
        animator.addUpdateListener(new ObjectAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int animatedValue = (int) animation.getAnimatedValue();
                mFab.setBackgroundTintList(ColorStateList.valueOf(animatedValue));
            }
        });
        animator.start();
    }

//    private void startHeadlessFragment(boolean stateLoss) {
//        HeadlessFragment headlessFragment =
//                (HeadlessFragment) getFragmentManager()
//                        .findFragmentById(R.id.headless_fragment_container);
//
//        if (headlessFragment == null) {
//            headlessFragment = new HeadlessFragment();
//            if (stateLoss) {
//                getFragmentManager().beginTransaction()
//                        .add(R.id.headless_fragment_container, headlessFragment).commitAllowingStateLoss();
//            } else {
//                getFragmentManager().beginTransaction()
//                        .add(R.id.headless_fragment_container, headlessFragment).commit();
//            }
//        }
//    }
//
//    private void stopHeadlessFragment() {
//        getSupportFragmentManager().executePendingTransactions();
//        Fragment fragmentById = getFragmentManager().
//                findFragmentById(R.id.headless_fragment_container);
//
//        if (fragmentById != null) {
//            getFragmentManager().beginTransaction()
//                    .remove(fragmentById).commit();
//        }
//    }

    private void showPermissionsRationale() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(getString(R.string.permissions_dialog_title));
        builder.setMessage(getString(R.string.permissions_dialog_message));

        builder.setPositiveButton(getString(R.string.permissions_dialog_positive), new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                Permissions.requestPermissions(MainActivity.this, PERMISSIONS_REQUEST_CODE);
            }

        });

        builder.setNegativeButton(getString(R.string.permissions_dialog_negative), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

//    private void displayFlashNotReacheble() {
//        mAdView.setVisibility(View.GONE);
//        mFab.setEnabled(false);
//        setFragment(R.id.content_fragment_container, WarningFragment.newInstance(), R.animator.fade_in_start, R.animator.fade_out_start);
//    }

    private void displayLicensesDialogFragment() {
        LicenseDialogFragment dialog = LicenseDialogFragment.newInstance();
        dialog.show(getSupportFragmentManager(), "LicensesDialog");
//        TODO: I should put a TAG
    }

    private void displayAboutFragment() {
        if (mCurrentFragmentId == R.layout.fragment_about) {
            return;
        }
        AboutFragment aboutFragment = AboutFragment.newInstance();
        mCurrentFragmentId = R.layout.fragment_about;
        if (mFlashIsOn) {
            fabAction(false);
        }
        setFragment(R.id.overlay_fragment_container, aboutFragment, R.animator.fade_in_start, R.animator.fade_out_start);
        mFab.hide();
        destroyAd();
    }

    private void setFragment(int containerViewId, Fragment fragment, Integer idOfInAnimation, Integer idOfOutAnimation) {

        if (fragment == null) {
            return;
        }

        FragmentTransaction fragmentTransaction =
                getFragmentManager().beginTransaction();

        if (null != idOfInAnimation && null != idOfOutAnimation) {
            fragmentTransaction.setCustomAnimations(idOfInAnimation, idOfOutAnimation);
        }

        fragmentTransaction.replace(containerViewId, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void removeFragment(int containerViewId, Integer idOfInAnimation, Integer idOfOutAnimation) {
        getFragmentManager().executePendingTransactions();
        Fragment fragmentById = getFragmentManager().
                findFragmentById(containerViewId);

        if (fragmentById == null) {
            return;
        }

        FragmentTransaction fragmentTransaction =
                getFragmentManager().beginTransaction();

        if (null != idOfInAnimation && null != idOfOutAnimation) {
            fragmentTransaction.setCustomAnimations(idOfInAnimation, idOfOutAnimation);
        }

        fragmentTransaction.remove(fragmentById);
        fragmentTransaction.commit();
    }

    private Intent getIntentForServiceByCheckedRadioId(int checkedRadioId) {
        switch (checkedRadioId) {
            case R.id.radio_mode_musical:
                return FlashlightIntentService.createIntentForActionMusical(this);

            case R.id.radio_mode_strobe:
                return FlashlightIntentService.createIntentForActionStrobe(this, getStrobeSeekBarValue());

            default:
                return null;
        }
    }

    private int getStrobeSeekBarValue() {
        if (mMainContentFragment != null) {
            return mMainContentFragment.getStrobeSeekBarValue();
        }
        return 0;
    }


    private int getCheckedRadioId() {
        if (mMainContentFragment != null) {
            return mMainContentFragment.getCheckedRadioId();
        }
        Log.d(TAG, "Fragment is null");
        return R.id.radio_mode_musical;
    }

    @Override
    public void onLicenseClicked() {
        displayLicensesDialogFragment();
    }

    @Override
    public void onRadioCheckedChanged(RadioGroup group, int checkedId) {
        if (!mFlashIsOn || !mFlashServiceIsBound) {
            return;
        }

        mFlashServiceIsBound = false;

        mFlashlightServiceIntent = getIntentForServiceByCheckedRadioId(checkedId);
        mFlashlightService.changeAction(getApplicationContext(), mServiceConnection, mFlashlightServiceIntent);
    }

    @Override
    public void onSeekBarProgressChanged(SeekBar seekBar, int i, boolean b) {
        if (!mFlashIsOn || !mFlashServiceIsBound) {
            return;
        }
        Log.d(TAG, "SeekBar value: " + i);
        mFlashlightService.setStrobeFrequency(i);
    }
}