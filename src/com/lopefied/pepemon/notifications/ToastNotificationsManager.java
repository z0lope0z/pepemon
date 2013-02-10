package com.lopefied.pepemon.notifications;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

/**
 * this class is still under construction
 * 
 * @author Lope Chupijay Emano
 * 
 */
public class ToastNotificationsManager implements NotificationsManager {
    public static final String TAG = ToastNotificationsManager.class
            .getSimpleName();
    private Toast mToast;

    public ToastNotificationsManager(Context context) {
        this.mToast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
    }

    @Override
    public void launchMessage(String message) {
        mToast.setText(message);
        mToast.show();
        // if (this.mToast.getView().getVisibility() != View.VISIBLE) {
        // }
    }

    @Override
    public Boolean isCurrentlyDisplaying() {
        return (this.mToast.getView().getVisibility() != View.VISIBLE);
    }

    @Override
    public void cancelMessage() {
        this.mToast.cancel();
    }

}
