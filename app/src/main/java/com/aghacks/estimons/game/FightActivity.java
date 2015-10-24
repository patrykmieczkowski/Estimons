package com.aghacks.estimons.game;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aghacks.estimons.Constants;
import com.aghacks.estimons.MainActivity;
import com.aghacks.estimons.R;
import com.aghacks.estimons.beacons.BeaconConnectionManager;
import com.aghacks.estimons.beacons.BeaconMotionManager;
import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;

import java.util.List;

public class FightActivity extends AppCompatActivity {
    public static final String TAG = FightActivity.class.getSimpleName();

    BeaconMotionManager manager;
    public TextView info, info2,info3;
    private RelativeLayout parent;
    private BeaconManager beaconManager;
    private BeaconConnectionManager beaconConnectionManager;
    private FloatingActionButton attackButton, runButton;
    //    private Beacon opponentBeacon = null;
    private ProgressBar barEstimon, barOpponent;
    private long lastNotification = -1;
    private long previousNotification = -1;
    private android.os.Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fight);
        info = (TextView) findViewById(R.id.textView4);
        info2 = (TextView) findViewById(R.id.textView6);
        info3 = (TextView) findViewById(R.id.textView8);
        parent = (RelativeLayout) findViewById(R.id.parent);
        parent.setBackgroundColor(Color.LTGRAY);

        injectViews();
//        setupTheGame();
        beaconManager = new BeaconManager(this);
//        beaconManager.setForegroundScanPeriod(500, 0);
        handler = new Handler();
    }

    private void setupTheGame() {
        Log.d(TAG, "setupTheGame ");
        GameEngine.clearAll();
        GameEngine.bindProgressBars(barEstimon, barOpponent);
        GameEngine.setup(new GameEngine.EndGameListener() {
            @Override
            public void onGameWon() {
                setupGameWon();
            }

            @Override
            public void onGameFailed() {
                setupGameFailed();
            }
        });
        GameEngine.start(this);
    }

    private void injectViews() {
        Log.d(TAG, "injectViews ");

        barEstimon = (ProgressBar) findViewById(R.id.yourPokeHp);
        barOpponent = (ProgressBar) findViewById(R.id.opponentHp);
        attackButton = (FloatingActionButton) findViewById(R.id.attack_fight_button);
        runButton = (FloatingActionButton) findViewById(R.id.run_fight__button);
        runButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick ");
                onBackPressed();
            }
        });

        attackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick ");
//                if (GameEngine.canUserMove && !GameEngine.gameEnd) {
//                    GameEngine.hit(false);
//                    opponentMove();
//                }
                startGame();
            }
        });


    }

    private void setupConnectionObservable() {
        Log.d(TAG, "setupConnectionObservable ");
        manager = new BeaconMotionManager(this);
        manager.setListener(new BeaconMotionManager.MotionChangeEventListener() {
            @Override
            public void broadcastActivity(final String s) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        info.setText(s);
                    }
                });
            }
        });
        manager.establishConnection();
    }

    public void startGame() {
        Log.d(TAG, "startGame ");

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                info2.setText("MOVE YOUR BEACON!");
                Constants.startAction = System.currentTimeMillis();
            }
        }, 1000);
        info2.setText("READY...");

    }

    @Override
    protected void onStart() {
        super.onStart();

        // Check if device supports Bluetooth Low Energy.
        if (!beaconManager.hasBluetooth()) {
            Toast.makeText(this, "Device does not have Bluetooth Low Energy", Toast.LENGTH_LONG).show();
            return;
        }

        // If Bluetooth is not enabled, let user enable it.
        if (!beaconManager.isBluetoothEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1234);
        } else {
            connectToService();
        }
    }

    private void connectToService() {
        Log.d(TAG, "connectToService ");
        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, List<Beacon> list) {
                Log.d(TAG, "onBeaconsDiscovered ");
                for (Beacon b : list) {
                    if (!b.getMacAddress().toStandardString().equals(Constants.CYAN_MAC_STRING)) {
                        beaconManager.stopRanging(Constants.ALL_ESTIMOTE_BEACONS_REGION);
                    }
                }
            }
        });
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                Log.d(TAG, "onServiceReady ");
                beaconManager.startRanging(Constants.ALL_ESTIMOTE_BEACONS_REGION);
            }
        });

        setupConnectionObservable();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1234) {
            if (resultCode == Activity.RESULT_OK) {
                connectToService();
            } else {
                Toast.makeText(this, "Bluetooth not enabled", Toast.LENGTH_LONG).show();
//                toolbar.setSubtitle("Bluetooth not enabled");
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        beaconManager.disconnect();
        super.onDestroy();
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

    private void setupGameFailed() {
        Log.d(TAG, "setupGameFailed ");
//        info.setTextSize(30.f);
        info.setText("YOU LOOSE...");
        GameEngine.gameEnd = true;
        parent.setBackgroundColor(Color.DKGRAY);
    }

    private void setupGameWon() {
        Log.d(TAG, "setupGameWon ");
//        info.setTextSize(30.f);
        info.setText("YOU WIN!!!");
        GameEngine.gameEnd = true;
        parent.setBackgroundColor(Color.DKGRAY);
    }

    @Override
    public void onBackPressed() {
        Constants.fightEscaped = true;
        Intent i = new Intent(FightActivity.this, MainActivity.class);
        startActivity(i);
        finish();
    }
}
