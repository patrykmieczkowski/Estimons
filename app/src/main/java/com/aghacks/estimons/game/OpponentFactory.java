package com.aghacks.estimons.game;

import android.util.Log;

import java.util.Random;

/**
 * Created by lukasz on 24.10.15.
 */
public class OpponentFactory {
    public static final String TAG = OpponentFactory.class.getSimpleName();
    private static String[] names = new String[]{"Blendzior", "Ryszard", "Zeenon", "Pou"};
    private static Random randInstance = new Random();

    public static String createRandomName() {
        Log.d(TAG, "createRandomName ");
        return names[randInstance.nextInt(names.length)];
    }
}
