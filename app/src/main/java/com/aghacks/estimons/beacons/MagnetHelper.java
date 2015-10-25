package com.aghacks.estimons.beacons;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.aghacks.estimons.Constants;
import com.aghacks.estimons.MainActivity;
import com.aghacks.estimons.R;
import com.aghacks.estimons.magneto.IconCallback;
import com.aghacks.estimons.magneto.Magnet;

/**
 * Created by lukasz on 25.10.15.
 *
 */
public class MagnetHelper {
    private MagnetHelper() {
    }

    public static final String TAG = MagnetHelper.class.getSimpleName();
    private static ImageView iconView;
    private static Magnet mMagnet;

    /**
     * Shows magnet
     *
     * @param handler
     * @param context
     * @param time
     */
    public static void show(Handler handler, final Context context, final long time) {
        Log.d(TAG, "showMagnet ");
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                iconView = new ImageView(context);
                iconView.setImageResource(R.drawable.tick);
                mMagnet = new Magnet.Builder(context)
                        .setIconView(iconView) // required
                        .withResource(0, R.drawable.tick)
                        .withResource(1, R.drawable.abc_btn_radio_to_on_mtrl_015)
                        .withResource(2, R.drawable.abc_btn_radio_material)

                        .setRemoveIconResId(R.drawable.trash)
                        .setIconCallback(new IconCallback() {
                            @Override
                            public void onFlingAway() {
                                Log.d(TAG, "onFlingAway ");
                            }

                            @Override
                            public void onMove(float x, float y) {
//                                Log.d(TAG, "onMove " + x + ", " + y);
                            }

                            @Override
                            public void onIconClick(View icon, float iconXPose, float iconYPose) {
                                //// TODO: 2015-10-14 new view for clickable notification
                                Intent intent = new Intent(context, MainActivity.class);
                                intent.putExtra(Constants.FEED_ME, true);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                context.startActivity(intent);
                                mMagnet.destroy();
                            }

                            @Override
                            public void onIconDestroyed() {
                            }
                        }).setRemoveIconShadow(R.drawable.bottom_shadow)
                        .setShouldFlingAway(true)
                        .setShouldStickToWall(true)
                        .setRemoveIconShouldBeResponsive(true)
                        .build();

                mMagnet.show();
            }
        }, time);
    }
}

