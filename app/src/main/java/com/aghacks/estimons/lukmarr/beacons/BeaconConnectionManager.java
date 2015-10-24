package com.aghacks.estimons.lukmarr.beacons;

import android.content.Context;
import android.util.Log;

import com.aghacks.estimons.Constants;
import com.estimote.sdk.EstimoteSDK;
import com.estimote.sdk.cloud.model.BeaconInfo;
import com.estimote.sdk.connection.BeaconConnection;
import com.estimote.sdk.exception.EstimoteDeviceException;

/**
 * Created by Patryk Mieczkowski on 24.10.15
 */
public class BeaconConnectionManager {

    private static final String TAG = BeaconConnectionManager.class.getSimpleName();

    private Context context;

    public BeaconConnectionManager(Context context) {
        this.context = context;
    }

    public void establishConnection() {
        BeaconConnection connection = new BeaconConnection(context, Constants.CYAN_MAC,
                new BeaconConnection.ConnectionCallback() {
                    @Override
                    public void onAuthorized(BeaconInfo beaconInfo) {
                        EstimoteSDK.initialize(context, "nie wiem co");

                    }

                    @Override
                    public void onConnected(BeaconInfo beaconInfo) {
//                        Log.d(TAG, "Authenticated to beacon. Info: " + beaconInfo);
//                        Log.d(TAG, "Advertising internal: " + connection.advertisingIntervalMillis().get());
//                        Log.d(TAG, "Broadcasting power: " + connection.broadcastingPower().get());
                    }

                    @Override
                    public void onAuthenticationError(EstimoteDeviceException e) {
                        Log.d(TAG, "Authentication Error: " + e);
                    }

                    @Override
                    public void onDisconnected() {
                        Log.d(TAG, "Disconnected");
                    }
                });
        connection.authenticate();

    }

}
