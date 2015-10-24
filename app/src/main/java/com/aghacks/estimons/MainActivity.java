package com.aghacks.estimons;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

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

        setUpEstimon();

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

        switch (id){
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

        setUpEstimon();
        return super.onOptionsItemSelected(item);
    }

    private void getViews(){
        estimonMainImage = (ImageView) findViewById(R.id.estimon_main_image);
        beaconFarText = (TextView) findViewById(R.id.beacon_far_text);
    }

    private void setUpEstimon(){

        switch (estimonRange){
            case Config.ESTIMON_FAR:
                // far
                if (estimonMainImage.getVisibility()==View.VISIBLE)
                    estimonMainImage.setVisibility(View.GONE);
                if (beaconFarText.getVisibility()==View.GONE)
                    beaconFarText.setVisibility(View.VISIBLE);
                break;
            case Config.ESTIMON_IMMEDIATE:
                // immediate
                if (estimonMainImage.getVisibility()==View.GONE)
                    estimonMainImage.setVisibility(View.VISIBLE);
                if (beaconFarText.getVisibility()==View.VISIBLE)
                    beaconFarText.setVisibility(View.GONE);

                estimonMainImage.getLayoutParams().height = 700;
                estimonMainImage.requestLayout();
                break;
            case Config.ESTIMON_NEAR:
                // near
                if (estimonMainImage.getVisibility()==View.GONE)
                    estimonMainImage.setVisibility(View.VISIBLE);
                if (beaconFarText.getVisibility()==View.VISIBLE)
                    beaconFarText.setVisibility(View.GONE);

                estimonMainImage.getLayoutParams().height = 1200;
                estimonMainImage.requestLayout();
                break;
            default:
                // error occurred
                break;
        }
    }
}
