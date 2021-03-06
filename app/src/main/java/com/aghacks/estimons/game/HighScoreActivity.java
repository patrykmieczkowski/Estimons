package com.aghacks.estimons.game;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.aghacks.estimons.R;
import com.aghacks.estimons.beacons.HighscoreAdapter;
import com.aghacks.estimons.util.RealmScore;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

public class HighScoreActivity extends AppCompatActivity {
    public static final String TAG = HighScoreActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_high_score);
        RecyclerView r = (RecyclerView) findViewById(R.id.my_recycler_view);
        Realm realm = Realm.getInstance(this);
        realm.beginTransaction();

        List<RealmScore> listR = realm.where(RealmScore.class).findAllSorted("score", true);

        List<String> list = new ArrayList<>();
        for (RealmScore s : listR) {
            list.add(String.valueOf(s.getScore()));
        }

        realm.commitTransaction();
        r.setLayoutManager(new LinearLayoutManager(this));
        r.setAdapter(new HighscoreAdapter(list));
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed ");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
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
