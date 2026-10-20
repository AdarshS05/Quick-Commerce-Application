package com.example.quickcommerceapplication.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.quickcommerceapplication.utils.Constants;

public class NetworkReceiver extends BroadcastReceiver {
    @Override public void onReceive(Context context, Intent intent) {
        ConnectivityManager manager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        android.net.Network network = manager.getActiveNetwork();
        NetworkCapabilities capabilities = network == null ? null :
                manager.getNetworkCapabilities(network);
        boolean connected = capabilities != null &&
                capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
        Intent status = new Intent(Constants.ACTION_NETWORK_STATUS);
        status.putExtra(Constants.EXTRA_IS_CONNECTED, connected);
        LocalBroadcastManager.getInstance(context).sendBroadcast(status);
    }
}

