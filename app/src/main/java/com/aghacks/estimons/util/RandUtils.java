package com.aghacks.estimons.util;

import java.util.Random;

/**
 * Created by lukasz on 24.10.15.
 */
public class RandUtils {

    private static final Random rand = new Random();

    public static long from(int leftBound, int rightBound) {
        return leftBound + rand.nextInt(rightBound - leftBound);
    }

    public static long getAIScore() {
        return 650 + rand.nextInt(500);
    }

}
