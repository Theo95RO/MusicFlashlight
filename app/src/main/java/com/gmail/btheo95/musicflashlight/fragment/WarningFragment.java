package com.gmail.btheo95.musicflashlight.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gmail.btheo95.musicflashlight.R;

public class WarningFragment extends Fragment {

    public WarningFragment() {
        // Required empty public constructor
    }

    public static WarningFragment newInstance() {
        return new WarningFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_warning, container, false);
    }
}
