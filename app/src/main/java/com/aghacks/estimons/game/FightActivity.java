package com.aghacks.estimons.game;

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

import com.aghacks.estimons.Constants;
import com.aghacks.estimons.MainActivity;
import com.aghacks.estimons.R;
import com.aghacks.estimons.beacons.BeaconConnectionManager;
import com.aghacks.estimons.eriks.ConnectionObservable;
import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.estimote.sdk.connection.BeaconConnection;
import com.estimote.sdk.connection.MotionState;
import com.estimote.sdk.connection.Property;
import com.estimote.sdk.exception.EstimoteDeviceException;

import java.util.List;

import rx.Subscriber;

public class FightActivity extends AppCompatActivity {
    public static final String TAG = FightActivity.class.getSimpleName();
    private TextView info;
    private RelativeLayout parent;
    private BeaconManager beaconManager;
    private BeaconConnectionManager beaconConnectionManager;
    //    private Beacon opponentBeacon = null;
    private ProgressBar barEstimon, barOpponent;
    private long lastNotification = -1;
    private long previousNotification = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fight);
        info = (TextView) findViewById(R.id.textView4);
        parent = (RelativeLayout) findViewById(R.id.parent);
        parent.setBackgroundColor(Color.LTGRAY);
        setupConnectionObservable();
        injectViews();
        setupTheGame();
        beaconManager = new BeaconManager(this);
//        beaconManager.setForegroundScanPeriod(500, 0);
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
        findViewById(R.id.run).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick ");
                Constants.fightEscaped = true;
                Intent i = new Intent(FightActivity.this, MainActivity.class);
                startActivity(i);
                finish();
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
    }

    private void setupConnectionObservable() {
        Log.d(TAG, "setupConnectionObservable ");
        ConnectionObservable.get(this, new BeaconConnectionManager.WpierdolListener() {
            @Override
            public void onWpierdol() {
                Log.d(TAG, "onWpierdol ");
            }
        }).subscribe(new Subscriber<BeaconConnection>() {
            @Override
            public void onCompleted() {
                Log.d(TAG, "onCompleted ");
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError ");
                e.printStackTrace();
            }

            @Override
            public void onNext(BeaconConnection beaconConnection) {
                Log.d(TAG, "onNext ");
                if (beaconConnection != null) {
                    beaconConnection.authenticate();
                    setAccelerometerCallback(beaconConnection);
                }
            }
        });

//        beaconConnectionManager = new BeaconConnectionManager(this);
//        beaconConnectionManager.setWpierdolListener(new BeaconConnectionManager.WpierdolListener() {
//            @Override
//            public void onWpierdol() {
//                Log.d(TAG, "wpierdol listener set ");
//                if (beaconConnectionManager != null && beaconConnectionManager.getConnection() != null) {
//                    thisConnection = beaconConnectionManager.getConnection();
//                    setAccelerometerCallback();
//                }
//            }
//        });
//        beaconConnectionManager.establishConnection();
    }

    private void setAccelerometerCallback(final BeaconConnection beaconConnection) {
        Log.d(TAG, "setAccelerometerCallback ");
        if (beaconConnection == null) {
            Log.e(TAG, "null connection object reference");
            return;
        }
        beaconConnection.edit().set(beaconConnection.motionDetectionEnabled(), true)
                .commit(new BeaconConnection.WriteCallback() {
                    @Override
                    public void onSuccess() {
                        Log.d(TAG, "onSuccess ");
                        // After on beacon connect all values are read so we can read them immediately and update UI.
                        String motionMessage = String.valueOf(
                                beaconConnection.motionDetectionEnabled().get()
                                        ? beaconConnection.motionState().get() : null);
                        Log.d(TAG, "onSuccess : motionMessage: " + motionMessage);
                        enableMotionListener(beaconConnection);
                    }

                    @Override
                    public void onError(EstimoteDeviceException exception) {
                        Log.e(TAG, "Failed to enable motion detection");
                        exception.printStackTrace();
                        Log.e(TAG, "error code: " + exception.errorCode);
                    }
                });
    }

    private void enableMotionListener(BeaconConnection beaconConnection) {
        Log.d(TAG, "enableMotionListener ");
        if (beaconConnection != null) {
            beaconConnection.setMotionListener(new Property.Callback<MotionState>() {
                @Override
                public void onValueReceived(final MotionState value) {
                    Log.d(TAG, "onValueReceived " + value.name());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            boolean moving = value == MotionState.NOT_MOVING;
                            info.setText(moving ? "MOVE!!!" : "HOLD ON!!!");
                            previousNotification = lastNotification;
                            lastNotification = System.currentTimeMillis();
                            Log.i(TAG, "twój refleks: " + (lastNotification - previousNotification) + " ms");
                        }
                    });
                }

                @Override
                public void onFailure() {
                    Log.e(TAG, "Unable to register motion listener");
                }
            });
        } else {
            Log.e(TAG, "sheeet... null connection object");
        }
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
