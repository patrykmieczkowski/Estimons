package com.aghacks.estimons;

import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.aghacks.estimons.game.FightActivity;
import com.estimote.sdk.MacAddress;
import com.estimote.sdk.Region;

/**
 * Created by lukasz on 24.10.15.
 */
public class Constants {
    public static final String TAG = Constants.class.getSimpleName();
    public static final String NEARABLE_ESTIMON = "NEARABLE_ESTIMON";
    public static final String CYAN_MAC_STRING = "CE:5C:52:22:57:EB";
    public static final String FEED_ME = "FEED_ME";
    public static MacAddress CYAN_MAC;
    public static final Region ALL_ESTIMOTE_BEACONS_REGION = new Region("rid", null, null, null);
    public static final Region CYAN_ESTIMOTE_REGION = new Region("rid", null, 22507, 21026);

    public static final int FIVE_MIN_IN_MS = 30000;
    public static final String STICKER_IDENTIFIER = "991aff45011b2f63";
    public static boolean fightEscaped = false;

    public static boolean connected = false;

    public static final int MAIN = 0;
    public static final int ZAWADIAKA = 1;
    public static final int FIGHT = 2;
    public static long endAction = 0;
    public static long startAction = 0;
    public static TextView userTextView;
    public static TextView opponentTextView;
    public static FightActivity activity;
    public static boolean startedGame = false;
    public static int userPoints = 0, oppPoints = 0;


    public static void setup() {
        userPoints = 0;
        oppPoints = 0;
        endAction = 0;
        startAction = 0;
    }

    public static boolean frozen = false;

    public static void calculateAccuracy() {
        if (frozen) return;
        startedGame = false;
        final long userScore = (endAction - startAction);
        Log.d(TAG, "calculateAccuracy : " + userScore);
        if (activity != null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "run ");
                    activity.calculate(userScore);
                }
            });
        }
    }

    public static void bindTextView(TextView textView1, TextView t2,
                                    ImageView userIm, ImageView oppIm,
                                    FightActivity act) {
        Log.d(TAG, "bindTextView ");
        userTextView = textView1;
        activity = act;
        opponentTextView = t2;
        userImageViewBar = userIm;
        oppImageViewBar = oppIm;

    }

    private static ImageView userImageViewBar, oppImageViewBar;

    public static Progressable progressable;

    public static void bindWhorl(Progressable _progressable) {
        progressable = _progressable;
    }

}
