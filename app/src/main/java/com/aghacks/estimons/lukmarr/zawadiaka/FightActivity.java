package com.aghacks.estimons.lukmarr.zawadiaka;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aghacks.estimons.R;

public class FightActivity extends AppCompatActivity {
    public static final String TAG = FightActivity.class.getSimpleName();
    private TextView info;
    private RelativeLayout parent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fight);
        info = (TextView) findViewById(R.id.textView4);
        parent = (RelativeLayout) findViewById(R.id.parent);
        parent.setBackgroundColor(Color.LTGRAY);
        ProgressBar barEstimon, barOpponent;
        barEstimon = (ProgressBar) findViewById(R.id.yourPokeHp);
        barOpponent = (ProgressBar) findViewById(R.id.opponentHp);
        findViewById(R.id.run).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick ");
                onBackPressed();
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

    private void opponentMove() {
        Log.d(TAG, "opponentMove ");
        info.setText("Opponent moves...");
        GameEngine.canUserMove = false;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                GameEngine.hit(true);
                if(GameEngine.gameEnd)
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
