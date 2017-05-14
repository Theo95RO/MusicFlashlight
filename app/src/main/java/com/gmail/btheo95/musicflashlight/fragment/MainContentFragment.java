package com.gmail.btheo95.musicflashlight.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.support.v7.widget.SwitchCompat;
import android.widget.TableLayout;

import com.gmail.btheo95.musicflashlight.R;
import com.gmail.btheo95.musicflashlight.util.Constants;

public class MainContentFragment extends Fragment {

    private static final String TAG = MainContentFragment.class.getSimpleName();

    private static final String STATE_CHECKED_RADIO = "state_checked_radio";
    private static final String STATE_SEEK_BAR = "state_seek_bar";

    private OnFragmentInteractionListener mListener;
    private Context mContext;
    private RadioGroup mRadioGroup;
    private SeekBar mStrobeSeekBar;
    private SwitchCompat mRunInBackgroundSwitch;

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

        outState.putInt(STATE_CHECKED_RADIO, mRadioGroup.getCheckedRadioButtonId());
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

        mRadioGroup.check(checkRadioId);
        mStrobeSeekBar.setProgress(strobeSeekBarProgress);

        if (checkRadioId != R.id.radio_mode_strobe) {
            mStrobeSeekBar.setEnabled(false);
        }
    }

    private void initialiseViews(View mainView) {
        initialiseRunInBackgroundSwitch(mainView);

        mRadioGroup = (RadioGroup) mainView.findViewById(R.id.radio_group_mode);
        mStrobeSeekBar = (SeekBar) mainView.findViewById(R.id.seek_bar_strobe);

        mStrobeSeekBar.setEnabled(false);
        mStrobeSeekBar.setMax(Constants.STROBE_SEEK_BAR_MAX);
        mStrobeSeekBar.setProgress(Constants.STROBE_SEEK_BAR_DEFAULT_VALUE);
        mStrobeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                                      @Override
                                                      public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                                                          mListener.onSeekBarProgressChanged(seekBar, calculateSeekBarProgress(i), b);
                                                      }

                                                      @Override
                                                      public void onStartTrackingTouch(SeekBar seekBar) {

                                                      }

                                                      @Override
                                                      public void onStopTrackingTouch(SeekBar seekBar) {

                                                      }
                                                  }
        );

        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        int flashModeRadioButtonPreference = sharedPreferences.getInt(Constants.PREFERENCE_FLASH_MODE_KEY, Constants.PREFERENCE_FLASH_MODE_DEFAULT);
        mRadioGroup.check(flashModeRadioButtonPreference);
        if (flashModeRadioButtonPreference == R.id.radio_mode_strobe) {
            mStrobeSeekBar.setEnabled(true);
        }


        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                sharedPreferences.edit()
                        .putInt(Constants.PREFERENCE_FLASH_MODE_KEY, checkedId)
                        .apply();

                switch (checkedId) {
                    case R.id.radio_mode_musical:
                        mStrobeSeekBar.setEnabled(false);
                        break;

                    case R.id.radio_mode_strobe:
                        mStrobeSeekBar.setEnabled(true);
                        break;

                    case R.id.radio_mode_torch:
                        mStrobeSeekBar.setEnabled(false);

                    default:
                        break;
                }
                mListener.onRadioCheckedChanged(group, checkedId);
            }

        });
    }

    private void initialiseRunInBackgroundSwitch(View mainView) {
        mRunInBackgroundSwitch = (SwitchCompat) mainView.findViewById(R.id.switch_run_in_background);
        TableLayout mRunInBackgroundContainer = (TableLayout) mainView.findViewById(R.id.run_in_background_container);

        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        boolean switchPreference = sharedPreferences.getBoolean(Constants.PREFERENCE_RUN_IN_BACKGROUND_KEY, Constants.PREFERENCE_RUN_IN_BACKGROUND_DEFAULT);

        mRunInBackgroundSwitch.setChecked(switchPreference);

        mRunInBackgroundSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                //notify activity
                mListener.onRunInBackgroundSwitchCheckChanged(compoundButton, b);

                //modify preference
                sharedPreferences.edit()
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
        return mRadioGroup.getCheckedRadioButtonId();
    }

    public interface OnFragmentInteractionListener {

        void onRadioCheckedChanged(RadioGroup group, int checkedId);

        void onSeekBarProgressChanged(SeekBar seekBar, int i, boolean b);

        void onRunInBackgroundSwitchCheckChanged(CompoundButton compoundButton, boolean checked);

    }

    public int getStrobeSeekBarValue() {
        return calculateSeekBarProgress(mStrobeSeekBar.getProgress());
    }
}
