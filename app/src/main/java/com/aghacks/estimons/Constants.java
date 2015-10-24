package com.aghacks.estimons;

import com.estimote.sdk.MacAddress;
import com.estimote.sdk.Region;

/**
 * Created by lukasz on 24.10.15.
 */
public class Constants {

    public static final String NEARABLE_ESTIMON = "NEARABLE_ESTIMON";
    public static final String CYAN_MAC_STRING = "CE:5C:52:22:57:EB";
    public static MacAddress CYAN_MAC;
    public static final Region ALL_ESTIMOTE_BEACONS_REGION = new Region("rid", null, null, null);
    public static final int FIVE_MIN_IN_MS = 30000;
}
