package com.aghacks.estimons.beacons;

import android.util.Log;

import com.aghacks.estimons.Constants;
import com.aghacks.estimons.game.FightActivity;
import com.estimote.sdk.cloud.model.BeaconInfo;
import com.estimote.sdk.connection.BeaconConnection;
import com.estimote.sdk.connection.MotionState;
import com.estimote.sdk.connection.Property;
import com.estimote.sdk.exception.EstimoteDeviceException;

/**
 * Created by Patryk Mieczkowski on 24.10.15
 */
public class BeaconMotionManager {
    public interface MotionChangeEventListener {
        void broadcastActivity(String s);
    }

    private MotionChangeEventListener listener;

    public MotionChangeEventListener getListener() {
        return listener;
    }

    public void setListener(MotionChangeEventListener listener) {
        this.listener = listener;
    }

    private static final String TAG = BeaconMotionManager.class.getSimpleName();
    private FightActivity context;
    private BeaconConnection connection;


    public BeaconConnection getConnection() {
        return connection;
    }

    public BeaconMotionManager(FightActivity activity) {
        this.context = activity;
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

                        connection.edit().set(connection.motionDetectionEnabled(), true)
//                                .set(connection.advertisingIntervalMillis(), 500)
                                .commit(new BeaconConnection.WriteCallback() {
                                    @Override
                                    public void onSuccess() {
                                        Log.d(TAG, "onSuccess ");
                                        // After on beacon connect all values are read so we can read them immediately and update UI.
                                        String motionMessage = String.valueOf(
                                                connection.motionDetectionEnabled().get()
                                                        ? connection.motionState().get() : null);
                                        if (listener != null)
                                            listener.broadcastActivity("MOTION DETECTION ENABLED");
                                        enableMotionListner();
                                    }

                                    @Override
                                    public void onError(EstimoteDeviceException exception) {
                                        Log.e(TAG, "Failed to enable motion detection");
                                        exception.printStackTrace();
                                        Log.e(TAG, "error code: " + exception.errorCode);
                                        if (listener != null)
                                            listener.broadcastActivity("MOTION DISABLED");
                                    }
                                });

                    }

                    @Override
                    public void onAuthenticationError(EstimoteDeviceException e) {
                        Log.d(TAG, "Authentication Error: " + e);
                        if (listener != null)
                            listener.broadcastActivity("AUTHENTICATION ERROR");
                    }

                    @Override
                    public void onDisconnected() {
                        Log.d(TAG, "Disconnected");
                        if (listener != null)
                            listener.broadcastActivity("DISCONNECTED");
                    }
                });
        connection.authenticate();
    }


    public void setMotionListener(Object o) {
        connection.setMotionListener(null);
    }

    private void enableMotionListner() {
        connection.setMotionListener(new Property.Callback<MotionState>() {
            @Override
            public void onValueReceived(final MotionState value) {
                Log.d(TAG, "onValueReceived ");
                if (listener != null) {
                    String s = value == MotionState.NOT_MOVING ? "MOVE" : "HOLD";
                    listener.broadcastActivity(s);
                }
            }

            @Override
            public void onFailure() {
                Log.e(TAG, "Unable to register motion listener");
            }
        });
    }
}
