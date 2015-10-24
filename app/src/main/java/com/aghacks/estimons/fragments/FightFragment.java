package com.aghacks.estimons.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aghacks.estimons.R;
import com.aghacks.estimons.beacons.BeaconConnectionManager;
import com.aghacks.estimons.beacons.BeaconMotionManager;
import com.aghacks.estimons.game.GameEngine;
import com.estimote.sdk.BeaconManager;

public class FightFragment extends Fragment {
    public static final String TAG = FightFragment.class.getSimpleName();
    BeaconMotionManager manager;
    public TextView info;
    private RelativeLayout parent;
    private BeaconManager beaconManager;
    private BeaconConnectionManager beaconConnectionManager;
    private FloatingActionButton attackButton, runButton;
    //    private Beacon opponentBeacon = null;
    private ProgressBar barEstimon, barOpponent;
    private long lastNotification = -1;
    private long previousNotification = -1;

    public static FightFragment newInstance( ) {
        FightFragment fragment = new FightFragment();

        return fragment;
    }

    public FightFragment() {
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
        info = (TextView) v.findViewById(R.id.textView4);

        parent = (RelativeLayout) v.findViewById(R.id.parent);

        barEstimon = (ProgressBar) v.findViewById(R.id.yourPokeHp);
        barOpponent = (ProgressBar) v.findViewById(R.id.opponentHp);
        attackButton = (FloatingActionButton) v.findViewById(R.id.attack_fight_button);
        runButton = (FloatingActionButton) v.findViewById(R.id.run_fight__button);
        runButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick ");
                getActivity().onBackPressed();
            }
        });

        attackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick ");
                if (GameEngine.canUserMove && !GameEngine.gameEnd) {
                    GameEngine.hit(false);
                    opponentMove();
                }
            }
        });
        return v;
    }

    private void opponentMove() {
        Log.d(TAG, "opponentMove ");
        info.setText("Opponent moves...");
        GameEngine.canUserMove = false;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                GameEngine.hit(true);
                if (GameEngine.gameEnd)
                    return;
                GameEngine.canUserMove = true;
                info.setText("Your move");
            }
        }, 3000);
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
