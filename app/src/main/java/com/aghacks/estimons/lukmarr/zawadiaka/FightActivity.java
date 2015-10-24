package com.aghacks.estimons.lukmarr.zawadiaka;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aghacks.estimons.R;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Nearable;

import java.util.List;

public class FightActivity extends AppCompatActivity {
    public static final String TAG = FightActivity.class.getSimpleName();
    private TextView info;
    private RelativeLayout parent;
    private BeaconManager beaconManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fight);
        info = (TextView) findViewById(R.id.textView4);
        parent = (RelativeLayout) findViewById(R.id.parent);
        parent.setBackgroundColor(Color.LTGRAY);
        ProgressBar barEstimon, barOpponent;
        barEstimon = (ProgressBar) findViewById(R.id.yourPokeHp);
        barOpponent = (ProgressBar) findViewById(R.id.opponentHp);
        findViewById(R.id.run).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick ");
                onBackPressed();
            }
        });

        findViewById(R.id.fight).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick ");
                if (GameEngine.canUserMove && !GameEngine.gameEnd) {
                    GameEngine.hit(false);
                    opponentMove();
                }
            }
        });

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
        beaconManager = new BeaconManager(this);
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
//        toolbar.setSubtitle("Scanning...");
        beaconManager.setNearableListener(new BeaconManager.NearableListener() {
            @Override
            public void onNearablesDiscovered(List<Nearable> list) {
                Log.i(TAG, "discovered " + list.size() + " nearables");
                for (Nearable nearable : list) {
                    Log.d(TAG, "next nearable: " + nearable);
                    final double x = nearable.xAcceleration;
                    final double y = nearable.yAcceleration;
                    final double z = nearable.zAcceleration;
                    final String mes = "is" + (nearable.isMoving ? " " : " not ") + "moving";
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            info.setText(x + "," + y + "," + z + ": " + mes);
                        }
                    });
                }
            }
        });
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                Log.d(TAG, "onServiceReady ");
                beaconManager.startNearableDiscovery();
            }
        });
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
}
