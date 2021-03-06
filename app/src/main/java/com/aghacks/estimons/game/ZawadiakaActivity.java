package com.aghacks.estimons.game;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.aghacks.estimons.R;
import com.aghacks.estimons.database.DetectedPoke;
import com.aghacks.estimons.Constants;
import com.aghacks.estimons.beacons.EstimonManager;
import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.tt.whorlviewlibrary.WhorlView;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

public class ZawadiakaActivity extends AppCompatActivity {
    public static final String TAG = ZawadiakaActivity.class.getSimpleName();
    private BeaconManager beaconManager;
    private WhorlView progressBar;
    private List<String> detectedPokesMacs = new ArrayList<>();
    private TextView rangingEstimonText;
    private boolean startedNewActivity = false;

    @Override
    protected void onStop() {
        beaconManager.disconnect();
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zawadiaka2);
        rangingEstimonText = (TextView) findViewById(R.id.ranging_estimon_text_zaw);
        Typeface myTypeface = Typeface.createFromAsset(getAssets(), "kindergarten.ttf");
        rangingEstimonText.setTypeface(myTypeface);
        progressBar = (WhorlView) findViewById(R.id.progressBarRanging);
        initDetectedPokes();

        showProgressBar(true);
        beaconManager = new BeaconManager(this);
    }

    private void initDetectedPokes() {
        Log.d(TAG, "initDetectedPokes ");
        Realm realm = Realm.getInstance(this);
        realm.beginTransaction();
        List<DetectedPoke> allPokes = realm.where(DetectedPoke.class).findAll();
        DetectedPoke yourPoke = realm.where(DetectedPoke.class).equalTo("estimonId", "1", false).findFirst();
        if (allPokes != null) {
            for (DetectedPoke poek : allPokes) {
                if (EstimonManager.canFightNow(yourPoke.getLastVisit(), poek.getLastVisit())) {
                    detectedPokesMacs.add(poek.getMac());
                }
            }
        }
        realm.commitTransaction();
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
        getMenuInflater().inflate(R.menu.menu_zawadiaka, menu);
        return true;
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
            startActivityForResult(enableBtIntent, 1234);
            overridePendingTransition(R.anim.slide_up, R.anim.slide_down);
        } else {
            connectToService();
        }
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

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private void connectToService() {
        Log.d(TAG, "connectToService ");

        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, List<Beacon> list) {
                for (final Beacon b : list) {

                    if (!startedNewActivity && !b.getMacAddress().toStandardString().equals(Constants.CYAN_MAC_STRING)) {
                        startedNewActivity = true;
                        showProgressBar(false);
                        Log.d(TAG, "discovered OPPONENT for wpierdol: " + b);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(ZawadiakaActivity.this, FightActivity.class);
                                intent.putExtra(Constants.NEARABLE_ESTIMON, b);
                                startActivity(intent);
                                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                finish();
                            }
                        });
                    }
                }
            }
        });

        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                beaconManager.startRanging(Constants.ALL_ESTIMOTE_BEACONS_REGION);
            }
        });
    }
}
