package com.aghacks.estimons.fragments;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aghacks.estimons.CommonActivity;
import com.aghacks.estimons.Constants;
import com.aghacks.estimons.R;
import com.aghacks.estimons.beacons.BeaconConnectionManager;

public class MainFragment extends Fragment {
    public static final String TAG = MainFragment.class.getSimpleName();
    private CommonActivity parentActivity;
    private ImageView estimonMainImage;
    private TextView nameText, countdownText;
    private FloatingActionButton attackButton, warmButton, eatButton;
    private BeaconConnectionManager beaconConnectionManager;
    private CountDownTimer countDownTimer;
    private boolean isCountDownTimerRunning = false;
    private Handler temperatureRefreshHandler;
    private float temperatureValue;

    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();

        return fragment;
    }

    public MainFragment() {
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
        View v = inflater.inflate(R.layout.activity_main, container, false);
        estimonMainImage = (ImageView) v.findViewById(R.id.estimon_main_image);

        Typeface myTypeface = Typeface.createFromAsset(getActivity().getAssets(), "kindergarten.ttf");
        nameText = (TextView) v.findViewById(R.id.estimon_name_text);
        countdownText = (TextView) v.findViewById(R.id.eat_countdown_timer_text);
        nameText.setTypeface(myTypeface);
        countdownText.setTypeface(myTypeface);

        attackButton = (FloatingActionButton) v.findViewById(R.id.attack_button);
        warmButton = (FloatingActionButton) v.findViewById(R.id.warm_button);
        eatButton = (FloatingActionButton) v.findViewById(R.id.eat_button);

        warmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                temperatureRefreshHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        parentActivity.refreshTemperature();
                    }
                });
            }
        });
        attackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((CommonActivity) getActivity()).switchToFragment(Constants.ZAWADIAKA);

                // if no nearby found show snackbar
//                Snackbar.make(view, "No nearby enemies found", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            }
        });
        eatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Constants.fightEscaped && isCountDownTimerRunning) {
                    estimonMainImage.setImageResource(R.drawable.najedzony1);
                    cancelCountDownTimer();
                    Snackbar.make(v, "Thank you for feeding me my lord!", Snackbar.LENGTH_LONG).show();
                }
            }
        });
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        checkBeaconInfo();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        parentActivity = (CommonActivity) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void checkBeaconInfo() {
        Log.d(TAG, "checkBeaconInfo ");

        beaconConnectionManager = new BeaconConnectionManager(getActivity());
        beaconConnectionManager.establishConnection();
    }

    private void eatCountdownTimer() {

        if (countdownText.getVisibility() == View.GONE)
            countdownText.setVisibility(View.VISIBLE);

        isCountDownTimerRunning = true;

        countDownTimer = new CountDownTimer(10000, 1000) {

            public void onTick(long millisUntilFinished) {
                countdownText.setText(String.valueOf(millisUntilFinished / 1000));
            }

            public void onFinish() {
                if (countdownText.getVisibility() == View.VISIBLE)
                    countdownText.setVisibility(View.GONE);
                isCountDownTimerRunning = false;
                estimonMainImage.setImageResource(R.drawable.lepa1);
            }
        }
                .start();

    }

    private void cancelCountDownTimer() {

        countDownTimer.cancel();
        isCountDownTimerRunning = false;

        if (countdownText.getVisibility() == View.VISIBLE)
            countdownText.setVisibility(View.GONE);

    }
}
