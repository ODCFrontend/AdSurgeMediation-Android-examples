package com.adsurge.mediation.sample;

import android.app.Activity;
import android.util.Log;
import android.view.Gravity;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import com.qq.e.tan.managers.TANAdSdk;
import com.qq.e.tan.managers.OnStartListener;
import com.qq.e.tan.util.AdError;
import com.qq.e.tan.api.TanAdFormat;
import com.qq.e.tan.api.ads.TanRewardedAd;
import com.qq.e.tan.api.TanRewardVideoAdListener;
import com.qq.e.tan.api.ads.TanInterstitialAd;
import com.qq.e.tan.api.TanInterstitialAdListener;
import com.qq.e.tan.api.ads.TanAdView;
import com.qq.e.tan.api.TanAdViewAdListener;

public class AdsurgeMediationAdManager {

    private static final String TAG = "AdsurgeMediationAdManager";
    private static volatile boolean sInitCalled = false;

    public static void initSdk(
            @NonNull android.content.Context context,
            @NonNull String appId,
            @NonNull OnStartListener listener) {
        synchronized (AdsurgeMediationAdManager.class) {
            if (!sInitCalled) {
                sInitCalled = true;
                TANAdSdk.getInstance().init(context, appId);
            }
        }

        TANAdSdk.getInstance().start(new OnStartListener() {
            @Override
            public void onStartComplete() {
                Log.d(TAG, "TAN SDK started successfully");
                listener.onStartComplete();
            }

            @Override
            public void onStartFailed(@NonNull AdError error) {
                Log.e(TAG, "TAN SDK start failed: " + error.errorCode + " - " + error.errorMsg);
                listener.onStartFailed(error);
            }
        }, succeededAdnNames -> Log.d(TAG, "ADNs initialized successfully: " + succeededAdnNames));
    }

    public static TanRewardedAd loadRewardedAd(
            @NonNull android.content.Context context,
            @NonNull String posId,
            @NonNull TanRewardVideoAdListener listener) {
        TanRewardedAd rewardedAd = new TanRewardedAd(context, posId);
        rewardedAd.setListener(listener);
        rewardedAd.loadAd();
        return rewardedAd;
    }

    public static boolean showRewardedAd(@NonNull TanRewardedAd rewardedAd, @NonNull Activity activity) {
        if (!rewardedAd.isValid()) {
            Log.w(TAG, "Rewarded ad is invalid, call loadAd() again");
            return false;
        }
        rewardedAd.showAd(activity);
        return true;
    }

    public static TanInterstitialAd loadInterstitialAd(
            @NonNull android.content.Context context,
            @NonNull String posId,
            @NonNull TanInterstitialAdListener listener) {
        TanInterstitialAd interstitialAd = new TanInterstitialAd(context, posId);
        interstitialAd.setListener(listener);
        interstitialAd.loadAd();
        return interstitialAd;
    }

    public static boolean showInterstitialAd(@NonNull TanInterstitialAd interstitialAd, @NonNull Activity activity) {
        if (!interstitialAd.isValid()) {
            Log.w(TAG, "Interstitial ad is invalid, call loadAd() again");
            return false;
        }
        interstitialAd.showAd(activity);
        return true;
    }

    public static TanAdView loadBannerAd(
            @NonNull android.content.Context context,
            @NonNull String posId,
            @NonNull FrameLayout container,
            @NonNull TanAdViewAdListener listener) {
        TanAdView bannerView = new TanAdView(context, posId);
        bannerView.setListener(listener);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                dpToPx(context, 320), dpToPx(context, 50));
        params.gravity = Gravity.CENTER;
        container.addView(bannerView, params);

        bannerView.loadAd();
        return bannerView;
    }

    public static TanAdView loadMrecAd(
            @NonNull android.content.Context context,
            @NonNull String posId,
            @NonNull FrameLayout container,
            @NonNull TanAdViewAdListener listener) {
        TanAdView mrecView = new TanAdView(context, posId, TanAdFormat.MREC);
        mrecView.setListener(listener);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                dpToPx(context, 300), dpToPx(context, 250));
        params.gravity = Gravity.CENTER;
        container.addView(mrecView, params);

        mrecView.loadAd();
        return mrecView;
    }

    private static int dpToPx(@NonNull android.content.Context context, int dp) {
        return (int) (dp * context.getResources().getDisplayMetrics().density + 0.5f);
    }
}
