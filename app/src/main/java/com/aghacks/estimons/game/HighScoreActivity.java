package com.aghacks.estimons.game;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.aghacks.estimons.R;
import com.aghacks.estimons.beacons.HighscoreAdapter;

import java.util.ArrayList;
import java.util.List;

public class HighScoreActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_high_score);
        RecyclerView r = (RecyclerView) findViewById(R.id.my_recycler_view);
        List<String> list = new ArrayList<>();
        list.add("100");
        list.add("220");
        list.add("432");
        list.add("513");
        r.setLayoutManager(new LinearLayoutManager(this));
        r.setAdapter(new HighscoreAdapter(list));
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
