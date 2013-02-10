package com.lopefied.pepemon.notifications;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class ToastNotificationsManager implements NotificationsManager {
    public static final String TAG = ToastNotificationsManager.class
            .getSimpleName();
    private Context mContext;
    private Toast mToast;

    public ToastNotificationsManager(Context context) {
        this.mContext = context;
        this.mToast = new Toast(context);
    }

    @Override
    public void launchMessage(String message) {
        Log.d(TAG, "mToast visible : " + mToast.getView().getVisibility());
        Log.d(TAG, "current toast duration : " + mToast.getDuration());
        if (mToast.getView().getVisibility() != View.VISIBLE) {
            mToast.setText(message);
            mToast.setDuration(Toast.LENGTH_SHORT);
        }
    }

    @Override
    public Boolean isCurrentlyDisplaying() {
        return (mToast.getView().getVisibility() != View.VISIBLE);
    }

    @Override
    public void cancelMessage() {
        mToast.cancel();
    }

}
