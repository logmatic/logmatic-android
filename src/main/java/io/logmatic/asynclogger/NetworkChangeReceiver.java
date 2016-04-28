package io.logmatic.asynclogger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Simple receiver. Catch any events about the network connectivity in
 * order to forward the current network state (online/offline) to the
 * logger registry
 */
public class NetworkChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {

        // Update the state for all loggers
        LoggerRegistry.updateNetworkStatus(isOnline(context));

    }

    /**
     * Check if the network is online (gsm or wifi)
     *
     * @param context
     * @return true if the network is online
     */
    public boolean isOnline(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        //should check null because in air plan mode it will be null
        return (netInfo != null && netInfo.isConnected());

    }
}
