package com.cedricapp.common;

import static android.content.Context.CONNECTIVITY_SERVICE;
import static com.cedricapp.common.Common.IMAGE_URL_INTERNET_TEST;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.annotation.NonNull;

import com.cedricapp.interfaces.InternetSpeedInterface;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ConnectionDetector {

    // bandwidth in kbps
    public static final int POOR_BANDWIDTH = 150;
    public static final int AVERAGE_BANDWIDTH = 550;
    public static final int GOOD_BANDWIDTH = 2000;
    private static double speed;

    static long startTime;
    static long endTime;
    static long fileSize;


    public static boolean isConnectedWithInternet(Context context) {
        try {
            ConnectivityManager con_manager = (ConnectivityManager)
                    context.getSystemService(CONNECTIVITY_SERVICE);

            if (con_manager.getActiveNetworkInfo() != null && con_manager.getActiveNetworkInfo().isAvailable() && con_manager.getActiveNetworkInfo().isConnected()) {
                return true;
            } else {
                return false;
            }
        }catch (Exception ex){
            if(Common.isLoggingEnabled){
                ex.printStackTrace();
            }
            return false;
        }
    }

    public static int getInternetSpeed(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        NetworkCapabilities nc = manager.getNetworkCapabilities(manager.getActiveNetwork());
        int downloadSpeed = nc.getLinkDownstreamBandwidthKbps();
        return downloadSpeed;
    }

    public static boolean checkConnectionSpeed(final int type, final int subType) {
        if (type == ConnectivityManager.TYPE_WIFI) {
            return true;
        } else if (type == ConnectivityManager.TYPE_MOBILE) {
            switch (subType) {
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                    return false; // ~ 50-100 kbps
                case TelephonyManager.NETWORK_TYPE_CDMA:
                    return false; // ~ 14-64 kbps
                case TelephonyManager.NETWORK_TYPE_EDGE:
                    return false; // ~ 50-100 kbps
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    return true; // ~ 400-1000 kbps
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    return true; // ~ 600-1400 kbps
                case TelephonyManager.NETWORK_TYPE_GPRS:
                    return false; // ~ 100 kbps
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                    return true; // ~ 2-14 Mbps
                case TelephonyManager.NETWORK_TYPE_HSPA:
                    return true; // ~ 700-1700 kbps
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                    return true; // ~ 1-23 Mbps
                case TelephonyManager.NETWORK_TYPE_UMTS:
                    return true; // ~ 400-7000 kbps
                /*
                 * Above API level 7, make sure to set android:targetSdkVersion
                 * to appropriate level to use these
                 */
                case TelephonyManager.NETWORK_TYPE_EHRPD: // API level 11
                    return true; // ~ 1-2 Mbps
                case TelephonyManager.NETWORK_TYPE_EVDO_B: // API level 9
                    return true; // ~ 5 Mbps
                case TelephonyManager.NETWORK_TYPE_HSPAP: // API level 13
                    return true; // ~ 10-20 Mbps
                case TelephonyManager.NETWORK_TYPE_IDEN: // API level 8
                    return false; // ~25 kbps
                case TelephonyManager.NETWORK_TYPE_LTE: // API level 11
                    return true; // ~ 10+ Mbps
                // Unknown
                case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                default:
                    return false;
            }
        } else {
            return false;
        }
    }

    public static void checkInternetSpeed(Context context, InternetSpeedInterface internetSpeed) {
        if (isConnectedWithInternet(context)) {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(IMAGE_URL_INTERNET_TEST)
                    .build();

            startTime = System.currentTimeMillis();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (!response.isSuccessful())
                        throw new IOException("Unexpected code " + response);

                    Headers responseHeaders = response.headers();
                    for (int i = 0, size = responseHeaders.size(); i < size; i++) {
                        Log.d(Common.LOG, responseHeaders.name(i) + ": " + responseHeaders.value(i));
                    }

                    InputStream input = response.body().byteStream();

                    try {
                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        byte[] buffer = new byte[1024];

                        while (input.read(buffer) != -1) {
                            bos.write(buffer);
                        }
                        byte[] docBuffer = bos.toByteArray();
                        fileSize = bos.size();

                    } finally {
                        input.close();
                    }

                    endTime = System.currentTimeMillis();


                    // calculate how long it took by subtracting endtime from starttime

                    double timeTakenMills = Math.floor(endTime - startTime);  // time taken in milliseconds
                    double timeTakenSecs = timeTakenMills / 1000;  // divide by 1000 to get time in seconds
                    final int kilobytePerSec = (int) Math.round(1024 / timeTakenSecs);

                    if (kilobytePerSec <= POOR_BANDWIDTH) {
                        // slow connection
                        if (Common.isLoggingEnabled) {
                            Log.d(Common.LOG, "Slow connection");
                        }
                    }

                    // get the download speed by dividing the file size by time taken to download
                    speed = fileSize / timeTakenMills;
                    SharedData.networkSpeed = kilobytePerSec;

                    if (Common.isLoggingEnabled) {
                        Log.d("NET_SPEED", "Time taken in secs: " + timeTakenSecs);
                        Log.d("NET_SPEED", "kilobyte per sec: " + kilobytePerSec);
                        Log.d("NET_SPEED", "kilobyte per sec global variable: " + SharedData.networkSpeed);
                        Log.d("NET_SPEED", "Download Speed: " + speed);
                        Log.d("NET_SPEED", "File size: " + fileSize);
                    }
                    internetSpeed.internetSpeed((int)SharedData.networkSpeed);
                }


                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    if (Common.isLoggingEnabled)
                        e.printStackTrace();
                    internetSpeed.internetSpeed(0);
                }
                /*@Override
                public void onResponse(Response response) throws IOException {
                    if (!response.isSuccessful())
                        throw new IOException("Unexpected code " + response);

                    Headers responseHeaders = response.headers();
                    for (int i = 0, size = responseHeaders.size(); i < size; i++) {
                        Log.d(Common.LOG, responseHeaders.name(i) + ": " + responseHeaders.value(i));
                    }

                    InputStream input = response.body().byteStream();

                    try {
                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        byte[] buffer = new byte[1024];

                        while (input.read(buffer) != -1) {
                            bos.write(buffer);
                        }
                        byte[] docBuffer = bos.toByteArray();
                        fileSize = bos.size();

                    } finally {
                        input.close();
                    }

                    endTime = System.currentTimeMillis();


                    // calculate how long it took by subtracting endtime from starttime

                    double timeTakenMills = Math.floor(endTime - startTime);  // time taken in milliseconds
                    double timeTakenSecs = timeTakenMills / 1000;  // divide by 1000 to get time in seconds
                    final int kilobytePerSec = (int) Math.round(1024 / timeTakenSecs);

                    if (kilobytePerSec <= POOR_BANDWIDTH) {
                        // slow connection
                        if (Common.isLoggingEnabled) {
                            Log.d(Common.LOG, "Slow connection");
                        }
                    }

                    // get the download speed by dividing the file size by time taken to download
                    speed = fileSize / timeTakenMills;
                    SharedData.networkSpeed = kilobytePerSec;

                    if (Common.isLoggingEnabled) {
                        Log.d(Common.LOG, "Time taken in secs: " + timeTakenSecs);
                        Log.d(Common.LOG, "kilobyte per sec: " + kilobytePerSec);
                        Log.d(Common.LOG, "kilobyte per sec global variable: " + SharedData.networkSpeed);
                        Log.d(Common.LOG, "Download Speed: " + speed);
                        Log.d(Common.LOG, "File size: " + fileSize);
                    }
                }*/
            });
        } else {
            internetSpeed.internetSpeed(0);
            if (Common.isLoggingEnabled)
                Log.d(Common.LOG, "No Internet connection");
        }
    }

    public static String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException ex) {
            if (Common.isLoggingEnabled)
                ex.printStackTrace();
        }
        return null;
    }

}
