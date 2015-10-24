package com.aghacks.estimons.beacons;

import android.content.Context;
import android.util.Log;

import com.aghacks.estimons.database.DetectedPoke;
import com.aghacks.estimons.Constants;
import com.aghacks.estimons.game.OpponentFactory;
import com.estimote.sdk.Beacon;

import java.util.UUID;

import io.realm.Realm;

/**
 * Created by lukasz on 24.10.15.
 */
public class EstimonManager {
    public static final String TAG = EstimonManager.class.getSimpleName();

    //prevent instantiation
    private EstimonManager() {
    }

    public static void updateYourPoke(long lastFightTime, Context context) {
        Realm r = Realm.getInstance(context);
        r.beginTransaction();
        DetectedPoke poke = r.where(DetectedPoke.class).equalTo("estimonId", "1", false).findFirst();
        poke.setLastVisit(lastFightTime);
        r.commitTransaction();
    }

    /**
     * @param yourMillis     - your estimon
     * @param opponentMillis
     * @return
     */
    public static boolean canFightNow(long yourMillis, long opponentMillis) {
        Log.d(TAG, "canFightNow ");
        //if last visit is longer than 5 mins
        long diffMillis = yourMillis - opponentMillis < 0 ? (opponentMillis - yourMillis) :
                (yourMillis - opponentMillis);
        return diffMillis > Constants.FIVE_MIN_IN_MS;
    }

    public static void addOpponentToDB(final Beacon beacon, Context context) {
        Log.d(TAG, "addOpponentToDB ");
        Realm realm = Realm.getInstance(context);
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Log.d(TAG, "execute : add opponent to realm");
                Log.d(TAG, "check if exists");
                DetectedPoke poke = realm.where(DetectedPoke.class).equalTo("mac",
                        beacon.getMacAddress().toStandardString(), false).findFirst();
                if (poke == null) {
                    Log.d(TAG, "add new opponent");
                    DetectedPoke p = realm.createObject(DetectedPoke.class);
                    p.setEstimonId(UUID.randomUUID().toString());
                    p.setLastVisit(System.currentTimeMillis());
                    p.setName(OpponentFactory.createRandomName());
                    p.setMac(beacon.getMacAddress().toStandardString());
                }
            }
        });
    }

    public static DetectedPoke getMyPoke(Context c) {
        Realm r = Realm.getInstance(c);
        r.beginTransaction();
        DetectedPoke p = r.where(DetectedPoke.class).equalTo("estimonId", "1", false).findFirst();
        r.commitTransaction();
        return p;
    }
}
