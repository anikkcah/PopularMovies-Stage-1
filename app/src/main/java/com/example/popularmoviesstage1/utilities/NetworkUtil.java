package com.example.popularmoviesstage1.utilities;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.os.Handler;

import androidx.annotation.IntRange;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Observable;

public class NetworkUtil {


    private static final String DEBUG_TAG = "NetworkStatusExample";
    //private static ConnectivityManager cm;

    //final ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

    boolean isWiFiConn = false;
    boolean isMobileConn = false;
    //Context ctx = null;

    /**
     * Helper function to check whether our device is connected to any network.
     * Extension to the current use is possible.
     * In this particular usage I only needed to check connected or not.
     *
     */

    @IntRange(from = 0, to = 3)
    public static int getConnectionType(Context context){
        int result = 0;
        //Returns connection type. 0:none; 1:wifi ; 2:mobile data
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(cm!=null){
                NetworkCapabilities capabilities = cm.getNetworkCapabilities(cm.getActiveNetwork());
                if(capabilities != null){
                    if(capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                        result = 1;
                    }
                    if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)){
                        result = 2;
                    }

                }
            }
        }
        else{
            if(cm != null){
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                if(activeNetwork != null){
                    //connected to the internet
                    if(activeNetwork.getType() == ConnectivityManager.TYPE_WIFI){
                        result = 1;
                    }
                    if(activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE){
                        result = 2;
                    }
                  }
            }
        }

        return result;
    }


}











