package com.pillowapps.liqear.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.pillowapps.liqear.LBApplication;
import com.pillowapps.liqear.helpers.Constants;

public class NetworkStateReceiver extends BroadcastReceiver {

    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(Constants.NETWORK_ACTION, intent.getData());
        Bundle extras = intent.getExtras();
        if (extras != null) i.putExtras(extras);
        LBApplication.getAppContext().sendBroadcast(i);
    }
}