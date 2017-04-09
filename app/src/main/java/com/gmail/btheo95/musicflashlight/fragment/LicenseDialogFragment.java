package com.gmail.btheo95.musicflashlight.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.webkit.WebView;

import com.gmail.btheo95.musicflashlight.R;

/**
 * Created by btheo on 3/29/2017.
 */

public class LicenseDialogFragment extends DialogFragment {

    public static LicenseDialogFragment newInstance() {
        return new LicenseDialogFragment();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        WebView view = (WebView) LayoutInflater.from(getActivity()).inflate(R.layout.fragment_dialog_license, null);
        view.loadUrl("file:///android_asset/license.html");
        return new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.license_dialog_title))
                .setView(view)
                .setPositiveButton(R.string.license_dialog_positive, null)
                .create();
    }

}
