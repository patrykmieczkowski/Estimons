package com.aghacks.estimons;

import android.app.Activity;
import android.util.Log;
import android.widget.TextView;

import com.aghacks.estimons.util.HighScoreUtils;
import com.estimote.sdk.MacAddress;
import com.estimote.sdk.Region;

/**
 * Created by lukasz on 24.10.15.
 */
public class Constants {
    public static final String TAG = Constants.class.getSimpleName();
    public static final String NEARABLE_ESTIMON = "NEARABLE_ESTIMON";
    public static final String CYAN_MAC_STRING = "CE:5C:52:22:57:EB";
    public static MacAddress CYAN_MAC;
    public static final Region ALL_ESTIMOTE_BEACONS_REGION = new Region("rid", null, null, null);
    public static final int FIVE_MIN_IN_MS = 30000;
    public static final String STICKER_IDENTIFIER = "991aff45011b2f63";
    public static boolean fightEscaped = false;

    public static boolean connected = false;

    public static final int MAIN = 0;
    public static final int ZAWADIAKA = 1;
    public static final int FIGHT = 2;
    public static long endAction = 0;
    public static long startAction = 0;
    public static TextView textView;
    public static Activity activity;
    public static boolean startedGame = false;

    public static void calculateAccuracy() {
        final long diff = (endAction - startAction);
        Log.d(TAG, "calculateAccuracy : " + diff);
        if (activity != null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "run ");
                    textView.setText(String.valueOf(diff));
                    HighScoreUtils.addToHighScore((int) diff, activity);
                }
            });
        }
    }

    public static void bindTextView(TextView textView1, Activity act) {
        Log.d(TAG, "bindTextView ");
        textView = textView1;
        activity = act;
    }

    public static Progressable progressable;

    public static void bindWhorl(Progressable _progressable) {
        progressable = _progressable;
    }

}
