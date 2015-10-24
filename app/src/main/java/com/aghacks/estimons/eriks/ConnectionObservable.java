package com.aghacks.estimons.eriks;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.aghacks.estimons.Constants;
import com.aghacks.estimons.beacons.BeaconConnectionManager;
import com.estimote.sdk.cloud.model.BeaconInfo;
import com.estimote.sdk.connection.BeaconConnection;
import com.estimote.sdk.exception.EstimoteDeviceException;

import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by lukasz on 24.10.15.
 */
public class ConnectionObservable {

    public static final String TAG = ConnectionObservable.class.getSimpleName();
    private static BeaconConnection connection = null;

    @NonNull
    public static Observable<BeaconConnection>
    get(Context context, @Nullable BeaconConnectionManager.WpierdolListener listener) {
        Log.d(TAG, "getBeaconConnection ");
        return Observable.just(alreadyGetBeaconConnection(context, listener))
                .observeOn(Schedulers.newThread())
                .subscribeOn(Schedulers.newThread());
    }

    private static BeaconConnection
    alreadyGetBeaconConnection(Context context, @Nullable final BeaconConnectionManager.WpierdolListener listener) {
        if (connection != null)
            return connection;
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
                        if (listener != null) {
                            listener.onWpierdol();
                        }
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
        return connection;
    }

}
