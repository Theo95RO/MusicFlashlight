package com.gmail.btheo95.musicflashlight.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gmail.btheo95.musicflashlight.R;

import java.util.Calendar;

import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

public class AboutFragment extends Fragment {

    private final static String TAG = AboutFragment.class.getSimpleName();

    private Context mContext;
    private AboutFragment.OnFragmentInteractionListener mListener;

    public AboutFragment() {
        // Required empty public constructor
    }

    public static AboutFragment newInstance() {
        return new AboutFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //bug fix on some devices
        mContext = container.getContext();
        if (mContext instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) mContext;
        } else {
            throw new RuntimeException(mContext.toString()
                    + " must implement OnFragmentInteractionListener");
        }

        return new AboutPage(mContext)
                .isRTL(false) // right to left
                .setDescription(getString(R.string.fragment_about_description, getString(R.string.app_name)))
                .setImage(R.mipmap.ic_launcher)
                .addItem(new Element().setTitle(getString(R.string.fragment_about_version, getString(R.string.version_number))))
                .addGroup(getString(R.string.fragment_about_connect_with_us_section))
                .addEmail(getString(R.string.email_feedback))
                .addPlayStore(getString(R.string.play_store_id))
                .addGroup(getString(R.string.fragment_about_copyright_section))
                .addItem(getCopyRightsElement())
                .addItem(getLicenseElement())
                .create();
    }

    private Element getLicenseElement() {
        Element copyLicenseElement = new Element();

        if (Build.VERSION.SDK_INT >= 21) {
            copyLicenseElement.setIconDrawable(R.drawable.ic_assignment_black_24dp);
        }
        copyLicenseElement.setTitle(getString(R.string.fragment_about_read_license));
        copyLicenseElement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onLicenseClicked();
            }
        });

        return copyLicenseElement;
    }

    private Element getCopyRightsElement() {

        Element copyRightsElement = new Element();

        if (Build.VERSION.SDK_INT < 21) {
            copyRightsElement.setTitle(getString(R.string.fragment_about_copyright_name_pre_lollipop, Calendar.getInstance().get(Calendar.YEAR)));
        } else {
            copyRightsElement.setTitle(getString(R.string.fragment_about_copyright_name, Calendar.getInstance().get(Calendar.YEAR)));
            copyRightsElement.setIconDrawable(R.drawable.ic_copyright_black_24dp);
        }

        return copyRightsElement;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onLicenseClicked();
    }


}
