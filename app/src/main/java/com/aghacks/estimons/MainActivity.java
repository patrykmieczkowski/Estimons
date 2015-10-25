package com.aghacks.estimons;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.aghacks.estimons.beacons.BeaconConnectionManager;
import com.aghacks.estimons.beacons.MagnetHelper;
import com.aghacks.estimons.database.DetectedPoke;
import com.aghacks.estimons.game.HighScoreActivity;
import com.aghacks.estimons.game.ZawadiakaActivity;
import com.estimote.sdk.Beacon;
import com.estimote.sdk.EstimoteSDK;
import com.estimote.sdk.connection.Property;

import io.realm.Realm;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    private ImageView estimonMainImage;
    private TextView nameText, countdownText;
    private FloatingActionButton attackButton, warmButton, eatButton;
    private BeaconConnectionManager beaconConnectionManager;
    private CountDownTimer countDownTimer;
    private boolean isCountDownTimerRunning = false;
    private Handler temperatureRefreshHandler;
    private float temperatureValue;

    boolean hasIntentFromMagnet = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        temperatureRefreshHandler = new Handler();
//        Constants.fightEscaped = false;
        EstimoteSDK.initialize(this, "estimons-mzy", "e2c71dee0a386b6a548d0cde0754384a");
        checkBeaconInfo();
        getViews();
        setUpEstimon((Beacon) getIntent().getParcelableExtra(Constants.NEARABLE_ESTIMON));
        if (getIntent().hasExtra(Constants.FEED_ME)) {
            hasIntentFromMagnet = true;
            estimonMainImage.setImageResource(R.drawable.glodny2);
        }
    }

    private void getViews() {
        estimonMainImage = (ImageView) findViewById(R.id.estimon_main_image);

        Typeface myTypeface = Typeface.createFromAsset(getAssets(), "kindergarten.ttf");
        nameText = (TextView) findViewById(R.id.estimon_name_text);
        countdownText = (TextView) findViewById(R.id.eat_countdown_timer_text);
        nameText.setTypeface(myTypeface);

        nameText.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent oo = new Intent(MainActivity.this, HighScoreActivity.class);
                startActivity(oo);
                return false;
            }
        });
        countdownText.setTypeface(myTypeface);

        attackButton = (FloatingActionButton) findViewById(R.id.attack_button);
        warmButton = (FloatingActionButton) findViewById(R.id.warm_button);
        eatButton = (FloatingActionButton) findViewById(R.id.eat_button);

        warmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                temperatureRefreshHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        refreshTemperature();
                    }
                });

            }
        });
        attackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, ZawadiakaActivity.class);
                startActivity(i);
                finish();

                // if no nearby found show snackbar
//                Snackbar.make(view, "No nearby enemies found", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            }
        });
        eatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Feed cat clicked");
                if (hasIntentFromMagnet || Constants.fightEscaped && isCountDownTimerRunning) {
                    estimonMainImage.setImageResource(R.drawable.najedzony2);
                    hasIntentFromMagnet = false;
                    new CountDownTimer(5000, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {

                        }

                        @Override
                        public void onFinish() {
                            estimonMainImage.setImageResource(R.drawable.zabawowy2);
                            new CountDownTimer(5000, 1000) {

                                @Override
                                public void onTick(long millisUntilFinished) {

                                }

                                @Override
                                public void onFinish() {
                                    estimonMainImage.setImageResource(R.drawable.basic1);
                                }
                            }.start();
                        }
                    }.start();
                    cancelCountDownTimer();
                    Snackbar.make(v, "Thank you for feeding me my lord!", Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    protected void onPause() {
        if (connectionNonNull()) {
            beaconConnectionManager.setMotionListener(null);
        }
        temperatureRefreshHandler.removeCallbacks(null);
        super.onPause();
    }

    private boolean tempNonNull() {
        return connectionNonNull() &&
                beaconConnectionManager.getConnection().temperature() != null &&
                beaconConnectionManager.getConnection().temperature().get() != null;
    }

    private boolean connectionNonNull() {
        return beaconConnectionManager != null &&
                beaconConnectionManager.getConnection() != null;
    }

    private void refreshTemperature() {
        Log.d(TAG, "refreshTemperature ");
        if (tempNonNull()) {
            beaconConnectionManager.getConnection().temperature().getAsync(new Property.Callback<Float>() {
                @Override
                public void onValueReceived(final Float value) {
                    if (isDestroyed()) {
                        return;
                    }
                    temperatureValue = value;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (attackButton != null && attackButton.isShown())
                                Snackbar.make(attackButton, "Temperature "
                                        + temperatureValue, Snackbar.LENGTH_LONG).show();
                        }
                    });
                }

                @Override
                public void onFailure() {
                    Log.d(TAG, "onFailure read temperature");
                }
            });
        } else {
            Log.e(TAG, "refreshTemperature failed due to null object reference");
            Snackbar.make(attackButton,
                    "Temperature not available yet", Snackbar.LENGTH_LONG).show();
        }
    }

    private void setUpEstimon(Beacon parcelableExtra) {

//        setYourPokemon(parcelableExtra);
//
//        Log.d(TAG, "setUpEstimon : all data from beacon:");
//        Log.d(TAG, "mac: " + parcelableExtra.getMacAddress());
//        Log.d(TAG, "proximityUUID: " + parcelableExtra.getProximityUUID());
//        Log.d(TAG, "major: " + parcelableExtra.getMajor());
//        Log.d(TAG, "minor: " + parcelableExtra.getMinor());
//        Log.d(TAG, "measuredPower: " + parcelableExtra.getMeasuredPower());
//        Log.d(TAG, "rssi: " + parcelableExtra.getRssi());
//        Log.d(TAG, "describeContents: " + parcelableExtra.describeContents());

        if (Constants.fightEscaped) {
            estimonMainImage.setImageResource(R.drawable.glodny1);
            eatCountdownTimer();
        } else {
            estimonMainImage.setImageResource(R.drawable.basic1);
        }

    }

    private void checkBeaconInfo() {
        Log.d(TAG, "checkBeaconInfo ");

        beaconConnectionManager = new BeaconConnectionManager(this);
        beaconConnectionManager.establishConnection();
    }

    private void setYourPokemon(Beacon parcelableExtra) {
        Log.d(TAG, "setYourPokemon ");

        Realm realm = Realm.getInstance(this);

        DetectedPoke pos = realm.where(DetectedPoke.class).equalTo("estimonId", "1", false).findFirst();
        if (pos == null)
            try {
                realm.beginTransaction();
                DetectedPoke poke = realm.createObject(DetectedPoke.class);
                poke.setName("Your Estimon");
                poke.setMac(parcelableExtra.getMacAddress().toStandardString());
                poke.setEstimonId("1");
                poke.setLastVisit(System.currentTimeMillis());
            } catch (Exception ex) {
                realm.cancelTransaction();
            } finally {
                realm.commitTransaction();
            }
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
                new CountDownTimer(20000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {

                    }

                    @Override
                    public void onFinish() {
                        estimonMainImage.setImageResource(R.drawable.basic1);
                    }
                }.start();

            }
        }.start();

    }

    private void cancelCountDownTimer() {

        countDownTimer.cancel();
        isCountDownTimerRunning = false;

        if (countdownText.getVisibility() == View.VISIBLE)
            countdownText.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed");

        MagnetHelper.show(new Handler(Looper.getMainLooper()), getApplicationContext(),
                R.drawable.male_logo, 7000);
        super.onBackPressed();
    }
}