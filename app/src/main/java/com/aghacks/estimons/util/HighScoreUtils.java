package com.aghacks.estimons.util;

import android.content.Context;
import android.util.Log;

import com.aghacks.estimons.beacons.HighscoreAdapter;

import java.util.UUID;

import io.realm.Realm;

/**
 * Created by lukasz on 24.10.15.
 */
public class HighScoreUtils {
    public static final String TAG = HighscoreAdapter.class.getSimpleName();

    private HighScoreUtils() {
    }

    public static void addToHighScore(Integer score, Context context) {
        Log.d(TAG, "addToHighScore ");
        Realm r = Realm.getInstance(context);
        r.beginTransaction();
        RealmScore rrr = r.createObject(RealmScore.class);
        rrr.setUuid(UUID.randomUUID().toString());
        rrr.setScore(score);
        r.commitTransaction();
    }
}
