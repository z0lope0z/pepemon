package com.lopefied.pepemon.playstore;

/**
 * Created by lope on 7/14/14.
 */

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;

class LicenseListener extends android.os.Binder {
    static final String LISTENER = "com.android.vending.licensing.ILicenseResultListener";

    public boolean onTransact(int op, Parcel in, Parcel reply, int flags) {
        if (op == 1) {
            in.enforceInterface(LISTENER);
            int code = in.readInt();
            String data = in.readString();
            String signature = in.readString();
            if (code == 0 || code == 2) {
                // LICENSED or LICENSED_OLD_KEY
            } else if (code == 1) {
                // NOT_LICENSED
            } else {
                // ERROR
            }
        }
        return true;
    }
}

public class License {
    public static final String SERVICE = "com.android.vending.licensing.ILicensingService";

    public static void check(final Context context) {
        context.bindService(
                new Intent(SERVICE),
                new ServiceConnection() {
                    public void onServiceConnected(ComponentName name, IBinder binder) {
                        Parcel d = Parcel.obtain();
                        try {
                            d.writeInterfaceToken("com.android.vending.licensing.ILicensingService");
                            d.writeLong(0);
                            d.writeString(context.getPackageName());
                            d.writeStrongBinder(new LicenseListener());
                            binder.transact(1, d, null, IBinder.FLAG_ONEWAY);
                        } catch (RemoteException e) {
                        }
                        d.recycle();
                    }

                    public void onServiceDisconnected(ComponentName name) {
                    }
                },
                Context.BIND_AUTO_CREATE);
    }
}