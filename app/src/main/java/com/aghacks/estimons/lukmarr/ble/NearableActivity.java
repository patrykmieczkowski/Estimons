package com.aghacks.estimons.lukmarr.ble;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.aghacks.estimons.R;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Nearable;
import com.tt.whorlviewlibrary.WhorlView;

import java.util.List;

public class NearableActivity extends AppCompatActivity {
    public static final String TAG = NearableActivity.class.getSimpleName();

    private TextView textView;
    private WhorlView progressBar;
    private BeaconManager beaconManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearable);
        textView = (TextView) findViewById(R.id.textView1);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_nearable, menu);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!beaconManager.hasBluetooth()) {
            Snackbar.make(progressBar, "Device does not have Bluetooth Low Energy", Snackbar.LENGTH_LONG).show();
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
    @Override
    protected void onDestroy() {
        beaconManager.disconnect();
        super.onDestroy();
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

    private void connectToService() {
//        toolbar.setSubtitle("Scanning...");

        beaconManager.setNearableListener(new BeaconManager.NearableListener() {
            @Override
            public void onNearablesDiscovered(List<Nearable> list) {
                Log.d(TAG, "onNearablesDiscovered " + list.size());
                for (Nearable nearable : list) {
                    Log.d(TAG, "next nearable: " + nearable);
//                    Log.d(TAG, "describeContents: " + nearable.describeContents());
//                    Log.d(TAG, "bootloaderVersion: " + nearable.bootloaderVersion);
//                    Log.d(TAG, "bootloaderVersion: " + nearable.toString());
                }
            }
        });
//        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
//            @Override
//            public void onBeaconsDiscovered(Region region, List<Beacon> list) {
//                showProgressBar(false);
//                for (Beacon b : list) {
//
//                    Log.d(TAG, "discovered mac: " + b.getMacAddress().toStandardString());
//                    if (b.getMacAddress().toStandardString().equals(Constants.CYAN_MAC)) {
//                        Log.d(TAG, "");
//                        Intent intent = new Intent(RangingActivity.this, MainActivity.class);
//                        intent.putExtra(Constants.NEARABLE_ESTIMON, b);
//                        startActivity(intent);
//                        finish();
//                    }
//                }
//            }
//        });

        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                Log.d(TAG, "onServiceReady : startNearableDiscovery");
                beaconManager.startNearableDiscovery();
//                beaconManager.startRanging(Constants.ALL_ESTIMOTE_BEACONS_REGION);
            }
        });
    }
}
