package com.aghacks.estimons.lukmarr.ble;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.aghacks.estimons.MainActivity;
import com.aghacks.estimons.R;
import com.aghacks.estimons.lukmarr.Constants;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Nearable;
import com.tt.whorlviewlibrary.WhorlView;

import java.util.List;

/**
 * Displays list of found nearables sorted by RSSI.
 * Starts new activity with selected nearable if activity was provided.
 *
 * @author wiktor.gworek@estimote.com (Wiktor Gworek)
 */
public class RangingActivity extends AppCompatActivity {

    private static final String TAG = RangingActivity.class.getSimpleName();

    public static final String EXTRAS_TARGET_ACTIVITY = "extrasTargetActivity";
    public static final String EXTRAS_NEARABLE = "extrasNearable";

    private static final int REQUEST_ENABLE_BT = 1234;

    private BeaconManager beaconManager;
    private WhorlView progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate ");
        setContentView(R.layout.ranging_main);
        progressBar = (WhorlView) findViewById(R.id.progressBar);
        showProgressBar(true);
        beaconManager = new BeaconManager(this);
    }

    public void showProgressBar(final boolean show) {
        Log.d(TAG, "showProgressBar " + show);
        int vis = show ? View.VISIBLE : View.GONE;
        if (show) progressBar.start();
        else progressBar.stop();
        progressBar.setVisibility(vis);
    }

    @Override
    protected void onDestroy() {
        beaconManager.disconnect();
        super.onDestroy();
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
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            connectToService();
        }
    }

    @Override
    protected void onStop() {
        beaconManager.disconnect();
        super.onStop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                connectToService();
            } else {
                Toast.makeText(this, "Bluetooth not enabled", Toast.LENGTH_LONG).show();
//                toolbar.setSubtitle("Bluetooth not enabled");
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void connectToService() {
//        toolbar.setSubtitle("Scanning...");

//        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
//            @Override
//            public void onBeaconsDiscovered(Region region, List<Beacon> list) {
//                showProgressBar(false);
//                for (Beacon b : list) {
//
//                    if (b.getMacAddress().toStandardString().equals(Constants.CYAN_MAC)) {
//                        Log.d(TAG, "discovered CYAN: " + b);
//                        Intent intent = new Intent(RangingActivity.this, MainActivity.class);
//                        intent.putExtra(Constants.NEARABLE_ESTIMON, b);
//                        startActivity(intent);
//                        finish();
//                    }
//                }
//            }
//        });
        beaconManager.setNearableListener(new BeaconManager.NearableListener() {
            @Override
            public void onNearablesDiscovered(List<Nearable> list) {
                for (Nearable b : list) {
                    Log.d(TAG, "discovered CYAN: " + b);
                    Intent intent = new Intent(RangingActivity.this, MainActivity.class);
                    intent.putExtra(Constants.NEARABLE_ESTIMON, b);
                    startActivity(intent);
                    finish();
                }
            }
        });
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
//                beaconManager.startRanging(Constants.ALL_ESTIMOTE_BEACONS_REGION);
                beaconManager.startNearableDiscovery();
            }
        });
    }
}