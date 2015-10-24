package com.aghacks.estimons.fragments;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aghacks.estimons.CommonActivity;
import com.aghacks.estimons.R;
import com.estimote.sdk.BeaconManager;
import com.tt.whorlviewlibrary.WhorlView;

import java.util.ArrayList;
import java.util.List;

public class ZawadiakaFragment extends Fragment {
    public static final String TAG = ZawadiakaFragment.class.getSimpleName();
    private BeaconManager beaconManager;
    private WhorlView progressBar;
    private List<String> detectedPokesMacs = new ArrayList<>();
    private TextView rangingEstimonText;
    private boolean startedNewActivity = false;
    private CommonActivity parentActivity;
    CommonActivity activity;

    public static ZawadiakaFragment newInstance() {
        ZawadiakaFragment fragment = new ZawadiakaFragment();

        return fragment;
    }

    public ZawadiakaFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.activity_zawadiaka2, container, false);
        rangingEstimonText = (TextView) v.findViewById(R.id.ranging_estimon_text_zaw);
        Typeface myTypeface = Typeface.createFromAsset(getActivity().getAssets(), "kindergarten.ttf");
        rangingEstimonText.setTypeface(myTypeface);
        progressBar = (WhorlView) v.findViewById(R.id.progressBarRanging);

        ((CommonActivity) getActivity()).showProgressBar(true);
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        parentActivity.restartScan();
    }

    @Override
    public void onAttach(Activity _activity) {
        super.onAttach(activity);
        parentActivity  = (CommonActivity)_activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
