package com.adsurge.mediation.sample.ads;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.adsurge.mediation.sample.R;
import com.adsurge.mediation.sample.SampleAdConfig;

import com.qq.e.tan.api.TanAd;
import com.qq.e.tan.api.TanInterstitialAdListener;
import com.qq.e.tan.api.ads.TanInterstitialAd;
import com.qq.e.tan.managers.TANAdSdk;
import com.qq.e.tan.util.AdError;

public class InterstitialAdActivity extends Activity {

    private static final String TAG = "InterstitialAdActivity";

    private TanInterstitialAd mInterstitialAd;
    private TextView mLogText;
    private Button mShowButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interstitial_ad);

        initViews();
        setupListeners();
    }

    private void initViews() {
        mLogText = findViewById(R.id.text_log);
        mShowButton = findViewById(R.id.btn_show);
        mShowButton.setEnabled(false);
    }

    private void setupListeners() {
        findViewById(R.id.btn_load).setOnClickListener(v -> loadInterstitialAd());
        mShowButton.setOnClickListener(v -> showInterstitialAd());
    }

    private void loadInterstitialAd() {
        appendLog("Loading interstitial ad...");
        mShowButton.setEnabled(false);

        mInterstitialAd = new TanInterstitialAd(this, SampleAdConfig.INTERSTITIAL_AD_UNIT_ID);
        mInterstitialAd.setListener(new TanInterstitialAdListener() {
                    @Override
                    public void onAdLoaded(TanAd tanAd) {
                        appendLog("Ad loaded");
                        mShowButton.setEnabled(true);
                    }

                    @Override
                    public void onAdDisplayed(TanAd tanAd) {
                        appendLog("Ad displayed");
                    }

                    @Override
                    public void onAdClicked(TanAd tanAd) {
                        appendLog("Ad clicked");
                    }

                    @Override
                    public void onAdLoadFailed(TanAd tanAd, AdError error) {
                        appendLog("Load failed: " + error.errorCode + " - " + error.errorMsg);
                    }

                    @Override
                    public void onAdShowFailed(TanAd tanAd, AdError error) {
                        appendLog("Show failed: " + error.errorCode + " - " + error.errorMsg);
                    }

                    @Override
                    public void onAdClosed(TanAd tanAd) {
                        appendLog("Ad closed");
                        mShowButton.setEnabled(false);
                    }
                });

        // Demonstrates setDevCustomInfo (per-ad custom info, reported with every
        // impression/click) and TANAdSdk.uploadAttributionInfo (one-off attribution).
        // See AdsurgeMediation Guidance §8. Replace with real values in production.
        TANAdSdk.uploadAttributionInfo("demo_attribution_info");
        mInterstitialAd.setDevCustomInfo("demo_custom_info");

        mInterstitialAd.loadAd();
    }

    private void showInterstitialAd() {
        if (mInterstitialAd == null) {
            appendLog("Load an ad first");
            return;
        }
        if (!mInterstitialAd.isValid()) {
            appendLog("Ad not ready, call loadAd() again");
            return;
        }
        mInterstitialAd.showAd(this);
    }

    private void appendLog(String message) {
        Log.d(TAG, message);
        mLogText.append(message + "\n");
    }
}
