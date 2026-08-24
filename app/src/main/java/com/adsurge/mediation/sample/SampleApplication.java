package com.adsurge.mediation.sample;

import android.app.Application;
import android.util.Log;

import com.qq.e.tan.managers.OnStartListener;
import com.qq.e.tan.util.AdError;

public class SampleApplication extends Application {

    private static final String TAG = "SampleApplication";

    private static volatile boolean sSdkReady = false;
    private static volatile String sInitError = null;

    @Override
    public void onCreate() {
        super.onCreate();

        initializeAdSurgeMediationSdk();
    }

    private void initializeAdSurgeMediationSdk() {
        AdsurgeMediationAdManager.initSdk(this, SampleAdConfig.APP_ID, new OnStartListener() {
            @Override
            public void onStartComplete() {
                sSdkReady = true;
                Log.d(TAG, "SDK initialized, ready to load ads");
            }

            @Override
            public void onStartFailed(AdError error) {
                sSdkReady = false;
                sInitError = error.errorCode + " - " + error.errorMsg;
                Log.e(TAG, "SDK initialization failed: " + sInitError);
            }
        });
    }

    public static boolean isSdkReady() {
        return sSdkReady;
    }

    public static String getInitError() {
        return sInitError;
    }
}
