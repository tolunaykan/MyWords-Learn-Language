package com.tolunaykandirmaz.mywords.Helper;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;

public class ConnectionHelper {

    public static Boolean isConnectedInternet(Context context){
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(manager == null) return false;

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            NetworkCapabilities capabilities = manager.getNetworkCapabilities(manager.getActiveNetwork());
            if(capabilities == null) return false;
            if(capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) return true;
            else if(capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) return true;
            else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) return true;
        }else{
            try {
                NetworkInfo networkInfo = manager.getActiveNetworkInfo();
                if(networkInfo != null && networkInfo.isConnected()){
                    return true;
                }
            }catch (Exception e){
                return false;
            }
        }
        return false;
    }
}
