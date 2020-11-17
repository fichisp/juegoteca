package com.juegoteca.util;

import android.content.Context;
import android.content.Intent;

public class NotificationHelper {

    private Context mContext;
    private static final String NOTIFICATION_CHANNEL_ID = "10001";

    NotificationHelper(Context context) {
        mContext = context;
    }

    void createNotification() {

        Utilidades utilidades = new Utilidades(mContext);
        utilidades.checkLaunchDates();
    }
}