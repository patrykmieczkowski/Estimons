package com.aghacks.estimons.game;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aghacks.estimons.Constants;
import com.aghacks.estimons.MainActivity;
import com.aghacks.estimons.Progressable;
import com.aghacks.estimons.R;
import com.aghacks.estimons.beacons.BeaconConnectionManager;
import com.aghacks.estimons.beacons.BeaconMotionManager;
import com.aghacks.estimons.util.RandUtils;
import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.tt.whorlviewlibrary.WhorlView;

import java.util.List;

public class FightActivity extends AppCompatActivity implements Progressable {
    public static final String TAG = FightActivity.class.getSimpleName();

    BeaconMotionManager manager;
    public TextView displayMessage, actionTextUser, actionTextOpp;//, info3;
    private RelativeLayout parent;
    private BeaconManager beaconManager;
    private BeaconConnectionManager beaconConnectionManager;
    private FloatingActionButton attackButton, runButton;
    //    private Beacon opponentBeacon = null;
    private ImageView barEstimon, barOpponent;
    private long lastNotification = -1;
    private long previousNotification = -1;
    private android.os.Handler handler;
    private WhorlView progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fight_new);
        parent = (RelativeLayout) findViewById(R.id.parent);
        parent.setBackgroundResource(R.drawable.main_activity_background);
        progressBar = (WhorlView) findViewById(R.id.progressBarRanging);
        Typeface myTypeface = Typeface.createFromAsset(getAssets(), "kindergarten.ttf");
        actionTextUser = (TextView) findViewById(R.id.action_text_user);
        actionTextOpp = (TextView) findViewById(R.id.action_text_opp);
        displayMessage = (TextView) findViewById(R.id.display_message_text);
        actionTextUser.setTypeface(myTypeface);
        actionTextOpp.setTypeface(myTypeface);
        displayMessage.setTypeface(myTypeface);

        injectViews();
//        setupTheGame();
        beaconManager = new BeaconManager(this);
//        beaconManager.setForegroundScanPeriod(500, 0);
        handler = new Handler();
        Constants.bindTextView(actionTextUser, actionTextOpp, barOpponent, barEstimon, this);
        Constants.bindWhorl(this);
        showProgressBar(true);
    }

    @Override
    protected void onStop() {
//        beaconConnectionManager.setMotionListener(null);
        beaconManager.disconnect();
        super.onStop();
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

        barEstimon = (ImageView) findViewById(R.id.yourPokeHp);
        barOpponent = (ImageView) findViewById(R.id.opponentHp);
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
                        displayMessage.setText(s);
                    }
                });
            }
        });
        manager.establishConnection();
    }

    public void startGame() {
        Log.d(TAG, "startGame ");
        Constants.startedGame = true;

        if (Constants.connected) {
            showProgressBar(false);
            displayMessage.setText("READY...");
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    displayMessage.setText("MOVE YOUR BEACON!");
                    Constants.startAction = System.currentTimeMillis();
                }
            }, RandUtils.from(1000, 6000));
        }
    }

    @Override
    public void showProgressBar(final boolean show) {
        Log.d(TAG, "showProgressBar ");
        int vis = show ? View.VISIBLE : View.GONE;
        progressBar.start();
        progressBar.setVisibility(vis);
    }

    @Override
    public void delay(int i) {
        Constants.frozen = false;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Constants.frozen = false;
            }
        }, i);
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
        displayMessage.setText("Opponent moves...");
        GameEngine.canUserMove = false;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                GameEngine.hit(true);
                if (GameEngine.gameEnd)
                    return;
                GameEngine.canUserMove = true;
                displayMessage.setText("Your move");
            }
        }, 3000);
    }

    private void setupGameFailed() {
        Log.d(TAG, "setupGameFailed ");
//        info.setTextSize(30.f);
        displayMessage.setText("YOU LOOSE...");
        GameEngine.gameEnd = true;
        parent.setBackgroundColor(Color.DKGRAY);
    }

    private void setupGameWon() {
        Log.d(TAG, "setupGameWon ");
//        info.setTextSize(30.f);
        displayMessage.setText("YOU WIN!!!");
        GameEngine.gameEnd = true;
        parent.setBackgroundColor(Color.DKGRAY);
    }

    @Override
    public void onBackPressed() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Constants.fightEscaped = true;
                Intent i = new Intent(FightActivity.this, MainActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.slide_up, R.anim.slide_down);
            }
        });

        finish();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
