package com.gmail.btheo95.musicflashlight.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TableLayout;

import com.gmail.btheo95.musicflashlight.R;
import com.gmail.btheo95.musicflashlight.runnable.MusicStrobe;
import com.gmail.btheo95.musicflashlight.util.Constants;

import net.cachapa.expandablelayout.ExpandableLayout;

public class MainContentFragment extends Fragment {

    private static final String TAG = MainContentFragment.class.getSimpleName();

    private static final String STATE_CHECKED_RADIO = "state_checked_radio";
    private static final String STATE_SEEK_BAR = "state_seek_bar";

    private OnFragmentInteractionListener mListener;
    private Context mContext;
    private SharedPreferences mSharedPreferences;
    private SwitchCompat mRunInBackgroundSwitch;
    private TableLayout mRunInBackgroundContainer;
    private RadioGroup mModeRadioGroup;
    private RadioGroup mMusicalSensibilityRadioGroup;
    private SeekBar mStrobeSeekBar;
    private SeekBar mMusicalSeekBar;
    //    private SwitchCompat mMusicalAutoSensSwitch;
    //    private TableLayout mMusicalAutoSensContainer;
    private ExpandableLayout mExpandableLayoutStrobeSettings;
    private ExpandableLayout mExpandableLayoutMusicalSettings;
    private ExpandableLayout mExpandableLayoutMusicalSeekbar;

    public MainContentFragment() {
    }

//    public static MainContentFragment newInstance() {
//        return new MainContentFragment();
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mainView = inflater.inflate(R.layout.fragment_main_content, container, false);

        initialiseContext(mainView);

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);

        initialiseViews(mainView);

        return mainView;
    }

    @Override
    public void onDetach() {
        mListener = null;
        mContext = null;
        super.onDetach();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(STATE_CHECKED_RADIO, mModeRadioGroup.getCheckedRadioButtonId());
        outState.putInt(STATE_SEEK_BAR, mStrobeSeekBar.getProgress());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState == null) {
            return;
        }

        int checkRadioId = savedInstanceState.getInt(STATE_CHECKED_RADIO);
        int strobeSeekBarProgress = savedInstanceState.getInt(STATE_SEEK_BAR);

        mModeRadioGroup.check(checkRadioId);
        mStrobeSeekBar.setProgress(strobeSeekBarProgress);

        handleFlashModeRadioButtonChange(checkRadioId, false);

    }

    private void initialiseViews(View mainView) {
        mModeRadioGroup = (RadioGroup) mainView.findViewById(R.id.radio_group_mode);
        mMusicalSensibilityRadioGroup = (RadioGroup) mainView.findViewById(R.id.radio_group_musical_sensibility);
        mStrobeSeekBar = (SeekBar) mainView.findViewById(R.id.seek_bar_strobe);
        mMusicalSeekBar = (SeekBar) mainView.findViewById(R.id.seek_bar_musical);
        mRunInBackgroundSwitch = (SwitchCompat) mainView.findViewById(R.id.switch_run_in_background);
        mRunInBackgroundContainer = (TableLayout) mainView.findViewById(R.id.run_in_background_container);
        mExpandableLayoutStrobeSettings = (ExpandableLayout) mainView.findViewById(R.id.expandable_layout_strobe_settings);
        mExpandableLayoutMusicalSettings = (ExpandableLayout) mainView.findViewById(R.id.expandable_layout_musical_settings);
        mExpandableLayoutMusicalSeekbar = (ExpandableLayout) mainView.findViewById(R.id.expandable_layout_musical_seek_bar);
//        mMusicalAutoSensSwitch = (SwitchCompat) mainView.findViewById(R.id.switch_auto_musical);
//        mMusicalAutoSensContainer = (TableLayout) mainView.findViewById(R.id.auto_music_sensibility_container);

        initialiseRunInBackgroundSwitch();
        initialiseMusicalSettings();
        initialiseStrobeSeekbar();
        initialiseMusicalSeekbar();
        initialiseModeRadioGroup();

//        if (mMusicalAutoSensSwitch.isChecked()) {
//            mExpandableLayoutMusicalSeekbar.collapse(false);
//        }

    }

    private void initialiseMusicalSeekbar() {
        mMusicalSeekBar.setMax(MusicStrobe.MAX_AVERAGE_AMPLITUDE);
        mMusicalSeekBar.setProgress(MusicStrobe.MIN_AVERAGE_AMPLITUDE);
        mMusicalSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                                       @Override
                                                       public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                                                           mListener.onMusicalSeekBarProgressChanged(seekBar, calculateSeekBarProgress(i), b);
                                                       }

                                                       @Override
                                                       public void onStartTrackingTouch(SeekBar seekBar) {

                                                       }

                                                       @Override
                                                       public void onStopTrackingTouch(SeekBar seekBar) {

                                                       }
                                                   }
        );
    }

    private void initialiseModeRadioGroup() {
        int flashModeRadioButtonPreference = mSharedPreferences.getInt(Constants.PREFERENCE_FLASH_MODE_KEY, Constants.PREFERENCE_FLASH_MODE_DEFAULT);
        mModeRadioGroup.check(flashModeRadioButtonPreference);
        handleFlashModeRadioButtonChange(flashModeRadioButtonPreference, false);

        mModeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                mSharedPreferences.edit()
                        .putInt(Constants.PREFERENCE_FLASH_MODE_KEY, checkedId)
                        .apply();

                handleFlashModeRadioButtonChange(checkedId);
                mListener.onFlashModeRadioCheckedChanged(group, checkedId);
            }

        });
    }

    private void initialiseStrobeSeekbar() {
        mStrobeSeekBar.setMax(Constants.STROBE_SEEK_BAR_MAX);
        mStrobeSeekBar.setProgress(Constants.STROBE_SEEK_BAR_DEFAULT_VALUE);
        mStrobeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                                      @Override
                                                      public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                                                          mListener.onStrobeSeekBarProgressChanged(seekBar, calculateSeekBarProgress(i), b);
                                                      }

                                                      @Override
                                                      public void onStartTrackingTouch(SeekBar seekBar) {

                                                      }

                                                      @Override
                                                      public void onStopTrackingTouch(SeekBar seekBar) {

                                                      }
                                                  }
        );
    }

    private void initialiseRunInBackgroundSwitch() {

        boolean switchPreference = mSharedPreferences.getBoolean(Constants.PREFERENCE_RUN_IN_BACKGROUND_KEY, Constants.PREFERENCE_RUN_IN_BACKGROUND_DEFAULT);

        mRunInBackgroundSwitch.setChecked(switchPreference);

        mRunInBackgroundSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                //notify activity
                mListener.onRunInBackgroundSwitchCheckChanged(compoundButton, b);

                //modify preference
                mSharedPreferences.edit()
                        .putBoolean(Constants.PREFERENCE_RUN_IN_BACKGROUND_KEY, b)
                        .apply();

            }
        });

        mRunInBackgroundContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mRunInBackgroundSwitch.isChecked()) {
                    mRunInBackgroundSwitch.setChecked(false);
                } else {
                    mRunInBackgroundSwitch.setChecked(true);
                }
            }
        });
    }

    private void initialiseMusicalSettings() {
        int musicalSensibilityRadioButtonPreference = mSharedPreferences.getInt(Constants.PREFERENCE_MUSICAL_SENSIBILITY_MODE_KEY,
                Constants.PREFERENCE_MUSICAL_SENSIBILITY_MODE_DEFAULT);
        mMusicalSensibilityRadioGroup.check(musicalSensibilityRadioButtonPreference);
        handleMusicalModeRadioButtonChange(musicalSensibilityRadioButtonPreference, false);

        mMusicalSensibilityRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                mSharedPreferences.edit()
                        .putInt(Constants.PREFERENCE_MUSICAL_SENSIBILITY_MODE_KEY, checkedId)
                        .apply();

                handleMusicalModeRadioButtonChange(checkedId);
                mListener.onMusicalModeRadioCheckedChanged(group, checkedId);
            }

        });
    }

    private void handleMusicalModeRadioButtonChange(int checkedId) {
        handleMusicalModeRadioButtonChange(checkedId, true);
    }

    private void handleMusicalModeRadioButtonChange(int checkedId, boolean shouldAnimate) {
        shouldAnimate = true; // TODO: Delete in future release of expandLay
        if (checkedId == R.id.radio_musical_sensibility_manual) {
            mExpandableLayoutMusicalSeekbar.expand(shouldAnimate);
        } else {
            mExpandableLayoutMusicalSeekbar.collapse(shouldAnimate);
        }
    }

    private void handleFlashModeRadioButtonChange(int checkedId) {
        handleFlashModeRadioButtonChange(checkedId, true);
    }

    private void handleFlashModeRadioButtonChange(int checkedId, boolean shouldAnimate) {
        shouldAnimate = true; // TODO: Delete in future release of expandLay
        if (checkedId != R.id.radio_mode_strobe) {
            mExpandableLayoutStrobeSettings.collapse(shouldAnimate);
        } else {
            mExpandableLayoutStrobeSettings.expand(shouldAnimate);
        }

        if (checkedId != R.id.radio_mode_musical) {
            mExpandableLayoutMusicalSettings.collapse(shouldAnimate);
        } else {
            mExpandableLayoutMusicalSettings.expand(shouldAnimate);
        }
    }

    private void initialiseContext(View container) {
        mContext = container.getContext();
        if (mContext instanceof MainContentFragment.OnFragmentInteractionListener) {
            mListener = (MainContentFragment.OnFragmentInteractionListener) mContext;
        } else {
            throw new RuntimeException(mContext.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    private int calculateSeekBarProgress(int value) {
        return Constants.STROBE_SEEK_BAR_MAX - value + Constants.STROBE_SEEK_BAR_MIN;
    }

    public int getCheckedRadioId() {
        return mModeRadioGroup.getCheckedRadioButtonId();
    }

    public interface OnFragmentInteractionListener {

        void onFlashModeRadioCheckedChanged(RadioGroup group, int checkedId);

        void onStrobeSeekBarProgressChanged(SeekBar seekBar, int i, boolean b);

        void onMusicalSeekBarProgressChanged(SeekBar seekBar, int i, boolean b);

        void onRunInBackgroundSwitchCheckChanged(CompoundButton compoundButton, boolean checked);

        void onMusicalModeRadioCheckedChanged(RadioGroup group, int checkedId);
    }

//    public static void expand(final View v) {
//        v.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        final int targetHeight = v.getMeasuredHeight();
//
//        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
//        v.getLayoutParams().height = 1;
//        v.setVisibility(View.VISIBLE);
//        Animation a = new Animation() {
//            @Override
//            protected void applyTransformation(float interpolatedTime, Transformation t) {
//                v.getLayoutParams().height = interpolatedTime == 1
//                        ? ViewGroup.LayoutParams.WRAP_CONTENT
//                        : (int) (targetHeight * interpolatedTime);
//                v.requestLayout();
//            }
//
//            @Override
//            public boolean willChangeBounds() {
//                return true;
//            }
//        };
//
//        // 1dp/ms
//        a.setDuration((int) (targetHeight / v.getContext().getResources().getDisplayMetrics().density) * 5);
//        v.startAnimation(a);
//    }
//
//    public static void collapse(final View v) {
//        final int initialHeight = v.getMeasuredHeight();
//
//        Animation a = new Animation() {
//            @Override
//            protected void applyTransformation(float interpolatedTime, Transformation t) {
//                if (interpolatedTime == 1) {
//                    v.setVisibility(View.GONE);
//                } else {
//                    v.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
//                    v.requestLayout();
//                }
//            }
//
//            @Override
//            public boolean willChangeBounds() {
//                return true;
//            }
//        };
//
//        // 1dp/ms
//        a.setDuration((int) (initialHeight / v.getContext().getResources().getDisplayMetrics().density) * 5);
//        v.startAnimation(a);
//    }

    public int getStrobeSeekBarValue() {
        return calculateSeekBarProgress(mStrobeSeekBar.getProgress());
    }
}
