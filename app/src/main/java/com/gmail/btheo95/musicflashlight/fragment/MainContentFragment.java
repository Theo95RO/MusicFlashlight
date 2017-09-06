package com.gmail.btheo95.musicflashlight.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TableLayout;

import com.gmail.btheo95.musicflashlight.R;
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
    private RadioGroup mMusicModeRadioGroup;
    private SeekBar mStrobeSeekBar;
    private SeekBar mMusicSeekBar;
    private ExpandableLayout mExpandableLayoutStrobeSettings;
    private ExpandableLayout mExpandableLayoutMusicSettings;
    private ExpandableLayout mExpandableLayoutMusicSeekbar;

    public MainContentFragment() {
    }

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
        mMusicModeRadioGroup = (RadioGroup) mainView.findViewById(R.id.radio_group_musical_sensibility);
        mStrobeSeekBar = (SeekBar) mainView.findViewById(R.id.seek_bar_strobe);
        mMusicSeekBar = (SeekBar) mainView.findViewById(R.id.seek_bar_musical);
        mRunInBackgroundSwitch = (SwitchCompat) mainView.findViewById(R.id.switch_run_in_background);
        mRunInBackgroundContainer = (TableLayout) mainView.findViewById(R.id.run_in_background_container);
        mExpandableLayoutStrobeSettings = (ExpandableLayout) mainView.findViewById(R.id.expandable_layout_strobe_settings);
        mExpandableLayoutMusicSettings = (ExpandableLayout) mainView.findViewById(R.id.expandable_layout_musical_settings);
        mExpandableLayoutMusicSeekbar = (ExpandableLayout) mainView.findViewById(R.id.expandable_layout_musical_seek_bar);

        initialisePaddingForLandscape(mainView);
        initialiseRunInBackgroundSwitch();
        initialiseMusicalSettings();
        initialiseStrobeSeekbar();
        initialiseMusicalSeekbar();
        initialiseModeRadioGroup();
    }

    private void initialisePaddingForLandscape(View mainView) {
        int orientation = getResources().getConfiguration().orientation;
        // ORIENTATION_SQUARE is uised for some old and bugged devices
        if (orientation == Configuration.ORIENTATION_LANDSCAPE
                || orientation == Configuration.ORIENTATION_SQUARE) {

            int padding_in_dp = 64;
            final float scale = getResources().getDisplayMetrics().density;
            int padding_in_px = (int) (padding_in_dp * scale + 0.5f);

            LinearLayout optionsListContainer = (LinearLayout) mainView.findViewById(R.id.options_list_container);
            optionsListContainer.setPadding(0, 0, 0, padding_in_px);
        }
    }

    public void checkDefaultFlashModeRadioBtn() {
        int flashModeRadioButtonPreference = mSharedPreferences.getInt(Constants.PREFERENCE_FLASH_MODE_KEY, Constants.PREFERENCE_FLASH_MODE_DEFAULT);
        if (flashModeRadioButtonPreference != R.id.radio_mode_musical
                && flashModeRadioButtonPreference != R.id.radio_mode_strobe
                && flashModeRadioButtonPreference != R.id.radio_mode_torch) {
            flashModeRadioButtonPreference = Constants.PREFERENCE_FLASH_MODE_DEFAULT;
        }
        mModeRadioGroup.check(flashModeRadioButtonPreference);
        handleFlashModeRadioButtonChange(flashModeRadioButtonPreference, false);
    }

    private void initialiseModeRadioGroup() {
        checkDefaultFlashModeRadioBtn();
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

    public void checkDefaultMusicSensRadioBtn() {
        int musicalSensibilityRadioButtonPreference = mSharedPreferences.getInt(Constants.PREFERENCE_MUSICAL_SENSIBILITY_MODE_KEY,
                Constants.PREFERENCE_MUSICAL_SENSIBILITY_MODE_DEFAULT);
        if (musicalSensibilityRadioButtonPreference != R.id.radio_musical_sensibility_auto
                && musicalSensibilityRadioButtonPreference != R.id.radio_musical_sensibility_manual) {
            musicalSensibilityRadioButtonPreference = Constants.PREFERENCE_MUSICAL_SENSIBILITY_MODE_DEFAULT;
        }
        mMusicModeRadioGroup.check(musicalSensibilityRadioButtonPreference);
        handleMusicalModeRadioButtonChange(musicalSensibilityRadioButtonPreference, false);
    }

    private void initialiseMusicalSettings() {
        checkDefaultMusicSensRadioBtn();
        mMusicModeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
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

    private void initialiseMusicalSeekbar() {
        int valuePreference = mSharedPreferences.getInt(Constants.PREFERENCE_MUSIC_SEEK_BAR_VALUE, Constants.MUSIC_SEEK_BAR_DEFAULT_VALUE);

        mMusicSeekBar.setMax(Constants.MUSIC_SEEK_BAR_MAX);
        mMusicSeekBar.setProgress(valuePreference);
        mMusicSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                                     @Override
                                                     public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                                                         mListener.onMusicalSeekBarProgressChanged(seekBar, calculateMusicSeekBarProgress(i), b);
                                                     }

                                                     @Override
                                                     public void onStartTrackingTouch(SeekBar seekBar) {
                                                     }

                                                     @Override
                                                     public void onStopTrackingTouch(SeekBar seekBar) {
                                                         mSharedPreferences.edit()
                                                                 .putInt(Constants.PREFERENCE_MUSIC_SEEK_BAR_VALUE, seekBar.getProgress())
                                                                 .apply();
                                                     }
                                                 }
        );
    }

    private void initialiseStrobeSeekbar() {
        int valuePreference = mSharedPreferences.getInt(Constants.PREFERENCE_STROBE_SEEK_BAR_VALUE, Constants.STROBE_SEEK_BAR_DEFAULT_VALUE);

        mStrobeSeekBar.setMax(Constants.STROBE_SEEK_BAR_MAX);
        mStrobeSeekBar.setProgress(valuePreference);
        mStrobeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                                      @Override
                                                      public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                                                          mListener.onStrobeSeekBarProgressChanged(seekBar, calculateStrobeSeekBarProgress(i), b);
                                                      }

                                                      @Override
                                                      public void onStartTrackingTouch(SeekBar seekBar) {

                                                      }

                                                      @Override
                                                      public void onStopTrackingTouch(SeekBar seekBar) {
                                                          mSharedPreferences.edit()
                                                                  .putInt(Constants.PREFERENCE_STROBE_SEEK_BAR_VALUE, seekBar.getProgress())
                                                                  .apply();
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

    private void handleMusicalModeRadioButtonChange(int checkedId) {
        handleMusicalModeRadioButtonChange(checkedId, true);
    }

    private void handleMusicalModeRadioButtonChange(int checkedId, boolean shouldAnimate) {
        if (checkedId == R.id.radio_musical_sensibility_manual) {
            mExpandableLayoutMusicSeekbar.expand(shouldAnimate);
        } else {
            mExpandableLayoutMusicSeekbar.collapse(shouldAnimate);
        }
    }

    private void handleFlashModeRadioButtonChange(int checkedId) {
        handleFlashModeRadioButtonChange(checkedId, true);
    }

    private void handleFlashModeRadioButtonChange(int checkedId, boolean shouldAnimate) {
        if (checkedId != R.id.radio_mode_strobe) {
            mExpandableLayoutStrobeSettings.collapse(shouldAnimate);
        } else {
            mExpandableLayoutStrobeSettings.expand(shouldAnimate);
        }

        if (checkedId != R.id.radio_mode_musical) {
            mExpandableLayoutMusicSettings.collapse(shouldAnimate);
        } else {
            mExpandableLayoutMusicSettings.expand(shouldAnimate);
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

    private int calculateStrobeSeekBarProgress(int value) {
        int returnedValue = Constants.STROBE_SEEK_BAR_MAX - value + Constants.STROBE_SEEK_BAR_MIN;
        Log.d(TAG, "Strobe seekBark value: " + returnedValue);
        return returnedValue;
    }

    private int calculateMusicSeekBarProgress(int value) {
        int returnedValue = Constants.MUSIC_SEEK_BAR_MAX - value + Constants.MUSIC_SEEK_BAR_MIN;
        Log.d(TAG, "Music seekBark value: " + returnedValue);
        return returnedValue;
    }

    public int getCheckedModeRadioId() {
        return mModeRadioGroup.getCheckedRadioButtonId();
    }

    public int getCheckedMusicModeRadioId() {
        return mMusicModeRadioGroup.getCheckedRadioButtonId();
    }

    public interface OnFragmentInteractionListener {

        void onFlashModeRadioCheckedChanged(RadioGroup group, int checkedId);

        void onStrobeSeekBarProgressChanged(SeekBar seekBar, int i, boolean b);

        void onMusicalSeekBarProgressChanged(SeekBar seekBar, int i, boolean b);

        void onRunInBackgroundSwitchCheckChanged(CompoundButton compoundButton, boolean checked);

        void onMusicalModeRadioCheckedChanged(RadioGroup group, int checkedId);
    }

    public int getStrobeSeekBarValue() {
        return calculateStrobeSeekBarProgress(mStrobeSeekBar.getProgress());
    }

    public int getMusicSeekBarValue() {
        return calculateMusicSeekBarProgress(mMusicSeekBar.getProgress());
    }
}
