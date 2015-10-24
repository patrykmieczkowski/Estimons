package com.aghacks.estimons.beacons;

import android.content.Context;
import android.util.Log;

import com.aghacks.estimons.Constants;
import com.estimote.sdk.cloud.model.BeaconInfo;
import com.estimote.sdk.connection.BeaconConnection;
import com.estimote.sdk.exception.EstimoteDeviceException;

/**
 * Created by Patryk Mieczkowski on 24.10.15
 */
public class BeaconConnectionManager {

    private static final String TAG = BeaconConnectionManager.class.getSimpleName();
    private Context context;
    private BeaconConnection connection;

    public interface WpierdolListener {
        void setMotionListenerAfterConnected(BeaconConnection connection);
    }
    public BeaconConnection getConnection() {
        return connection;
    }

    public BeaconConnectionManager(Context context) {
        this.context = context;
    }

    public void establishConnection() {
        Log.d(TAG, "establishConnection ");
        connection = new BeaconConnection(context, Constants.CYAN_MAC,
                new BeaconConnection.ConnectionCallback() {
                    @Override
                    public void onAuthorized(BeaconInfo beaconInfo) {
                        Log.d(TAG, "onAuthorized ");
//                        EstimoteSDK.initialize(context, "estimons-mzy", "e2c71dee0a386b6a548d0cde0754384a");
//                        connection.authenticate();
                    }

                    @Override
                    public void onConnected(BeaconInfo beaconInfo) {
                        Log.d(TAG, "Authenticated to beacon. Info: " + beaconInfo);
                        Log.d(TAG, "Advertising internal: " + connection.advertisingIntervalMillis().get());
                        Log.d(TAG, "Broadcasting power: " + connection.broadcastingPower().get());
                        Log.d(TAG, "beaconInfo temp: " + String.valueOf(connection.temperature().get()));

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

    public void setMotionListener(Object o) {
        connection.setMotionListener(null);
    }
}
