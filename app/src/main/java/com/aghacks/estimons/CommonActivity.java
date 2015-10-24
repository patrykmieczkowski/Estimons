package com.aghacks.estimons;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.aghacks.estimons.beacons.BeaconConnectionManager;
import com.aghacks.estimons.fragments.FightFragment;
import com.aghacks.estimons.fragments.MainFragment;
import com.aghacks.estimons.fragments.ZawadiakaFragment;
import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.EstimoteSDK;
import com.estimote.sdk.Region;
import com.estimote.sdk.connection.Property;
import com.tt.whorlviewlibrary.WhorlView;

import java.util.List;

public class CommonActivity extends AppCompatActivity {
    public static final String TAG = CommonActivity.class.getSimpleName();
    private WhorlView progressBar;
    private BeaconConnectionManager beaconConnectionManager;
    private BeaconManager beaconManager;
    private Handler temperatureRefreshHandler;
    private RelativeLayout lay;
    float temperatureValue;
    public boolean banToScan = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common);
        progressBar = (WhorlView) findViewById(R.id.progressBarRanging);
        lay = (RelativeLayout) findViewById(R.id.parent);
        setInitialFragment();
        checkBeaconInfo();
    }

    public void refreshTemperature() {
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
                                Snackbar.make(lay, "Temperature "
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
            Snackbar.make(lay,
                    "Temperature not available yet", Snackbar.LENGTH_LONG).show();
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
                    if (!banToScan && !b.getMacAddress().toStandardString().equals(Constants.CYAN_MAC_STRING)) {
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

    private void checkBeaconInfo() {
        Log.d(TAG, "checkBeaconInfo ");
        EstimoteSDK.initialize(this, "estimons-mzy", "e2c71dee0a386b6a548d0cde0754384a");
        beaconConnectionManager = new BeaconConnectionManager(this);
        beaconConnectionManager.establishConnection();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_common, menu);
        return true;
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

    @Override
    protected void onPause() {
        if (connectionNonNull()) {
            beaconConnectionManager.setMotionListener(null);
        }
        temperatureRefreshHandler.removeCallbacks(null);
        super.onPause();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setInitialFragment() {
        getSupportFragmentManager().beginTransaction()
                .add(R.id.placeholder, MainFragment.newInstance())
                .commit();
    }

    public void showProgressBar(final boolean show) {
        Log.d(TAG, "showProgressBar " + show);
        int vis = show ? View.VISIBLE : View.GONE;
        if (show) progressBar.start();
        else progressBar.stop();
        progressBar.setVisibility(vis);
    }

    public void switchToFragment(int id) {
        Log.d(TAG, "switchToFragment ");
        switch (id) {
            case Constants.MAIN: {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.placeholder, MainFragment.newInstance())
                        .commit();
                break;
            }
            case Constants.FIGHT: {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.placeholder, FightFragment.newInstance())
                        .commit();
                break;
            }
            case Constants.ZAWADIAKA: {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.placeholder, ZawadiakaFragment.newInstance())
                        .commit();
                break;
            }
        }
    }
    public void restartScan() {
        Log.d(TAG, "restartScan ");
    }
}
