package com.gmail.btheo95.musicflashlight.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gmail.btheo95.musicflashlight.runnable.MusicStrobe;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class HeadlessFragment extends Fragment {

    private static final String TAG = HeadlessFragment.class.getSimpleName();

    private Executor mExecutor;
    private MusicStrobe mMusicStrobe;

    public HeadlessFragment() {
        // Required empty public constructor
    }

    public static HeadlessFragment newInstance() {
        return new HeadlessFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");
        setRetainInstance(true);

        mExecutor = Executors.newSingleThreadExecutor();
        mMusicStrobe = new MusicStrobe();
        mExecutor.execute(mMusicStrobe);

//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
    }

    @Override
    public void onDestroy() {

        mMusicStrobe.shutdown();
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        View view = inflater.inflate(R.layout.fragment_warning, container, false);
//        Button fab = (Button) view.findViewById(R.id.fab);
//        fab.setOnClickListener(this);
        Log.d(TAG, "onCreateView()");
        return null;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated()");
    }

}
