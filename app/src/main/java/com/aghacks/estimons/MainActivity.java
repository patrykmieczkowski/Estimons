package com.aghacks.estimons;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.aghacks.estimons.database.DetectedPoke;
import com.aghacks.estimons.lukmarr.Constants;
import com.aghacks.estimons.lukmarr.zawadiaka.ZawadiakaActivity;
import com.estimote.sdk.Beacon;
import com.estimote.sdk.Nearable;

import io.realm.Realm;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = MainActivity.class.getSimpleName();
    private ImageView estimonMainImage;
    private TextView beaconFarText;
    private int estimonRange = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getViews();
        Nearable a;
        Beacon b;

        setUpEstimon((Beacon) getIntent().getParcelableExtra(Constants.NEARABLE_ESTIMON));

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.beacon_far_menu:
                estimonRange = Config.ESTIMON_FAR;
                break;
            case R.id.beacon_immediate_menu:
                estimonRange = Config.ESTIMON_IMMEDIATE;
                break;
            case R.id.beacon_near_menu:
                estimonRange = Config.ESTIMON_NEAR;
                break;
        }

        setUpEstimon((Beacon) getIntent().getParcelableExtra(Constants.NEARABLE_ESTIMON));
        return super.onOptionsItemSelected(item);
    }

    private void getViews() {
        estimonMainImage = (ImageView) findViewById(R.id.estimon_main_image);
        beaconFarText = (TextView) findViewById(R.id.beacon_far_text);
        estimonMainImage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent i = new Intent(MainActivity.this, ZawadiakaActivity.class);
                startActivity(i);
                return false;
            }
        });
    }

    private void setUpEstimon(Beacon parcelableExtra) {

        setYourPokemon(parcelableExtra);

        Log.d(TAG, "setUpEstimon : all data from beacon:");
        Log.d(TAG, "mac: " + parcelableExtra.getMacAddress());
        Log.d(TAG, "proximityUUID: " + parcelableExtra.getProximityUUID());
        Log.d(TAG, "major: " + parcelableExtra.getMajor());
        Log.d(TAG, "minor: " + parcelableExtra.getMinor());
        Log.d(TAG, "measuredPower: " + parcelableExtra.getMeasuredPower());
        Log.d(TAG, "rssi: " + parcelableExtra.getRssi());
        Log.d(TAG, "describeContents: " + parcelableExtra.describeContents());


        switch (estimonRange) {
            case Config.ESTIMON_FAR:
                // far
                if (estimonMainImage.getVisibility() == View.VISIBLE)
                    estimonMainImage.setVisibility(View.GONE);
                if (beaconFarText.getVisibility() == View.GONE)
                    beaconFarText.setVisibility(View.VISIBLE);
                break;
            case Config.ESTIMON_IMMEDIATE:
                // immediate
                if (estimonMainImage.getVisibility() == View.GONE)
                    estimonMainImage.setVisibility(View.VISIBLE);
                if (beaconFarText.getVisibility() == View.VISIBLE)
                    beaconFarText.setVisibility(View.GONE);

                estimonMainImage.getLayoutParams().height = 700;
                estimonMainImage.requestLayout();
                break;
            case Config.ESTIMON_NEAR:
                // near
                if (estimonMainImage.getVisibility() == View.GONE)
                    estimonMainImage.setVisibility(View.VISIBLE);
                if (beaconFarText.getVisibility() == View.VISIBLE)
                    beaconFarText.setVisibility(View.GONE);

                estimonMainImage.getLayoutParams().height = 1200;
                estimonMainImage.requestLayout();
                break;
            default:
                // error occurred
                break;
        }
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
}
