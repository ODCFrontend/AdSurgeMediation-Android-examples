package com.adsurge.mediation.sample.ads;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.adsurge.mediation.sample.AdsurgeMediationAdManager;
import com.adsurge.mediation.sample.R;
import com.adsurge.mediation.sample.SampleAdConfig;

import com.qq.e.tan.api.TanAd;
import com.qq.e.tan.api.TanInterstitialAdListener;
import com.qq.e.tan.api.ads.TanInterstitialAd;
import com.qq.e.tan.managers.TANAdSdk;
import com.qq.e.tan.util.AdError;

public class InterstitialAdActivity extends AppCompatActivity {

    private static final String TAG = "InterstitialAdActivity";

    private TanInterstitialAd mInterstitialAd;
    private TextView mLogText;
    private Button mShowButton;
    private String mDevCustomInfo;

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
        findViewById(R.id.btn_custom_info).setOnClickListener(v -> showCustomInfoDialog());
    }

    private void loadInterstitialAd() {
        appendLog("Loading interstitial ad...");
        mShowButton.setEnabled(false);

        mInterstitialAd = AdsurgeMediationAdManager.loadInterstitialAd(this, SampleAdConfig.INTERSTITIAL_AD_UNIT_ID,
                new TanInterstitialAdListener() {
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

        if (!TextUtils.isEmpty(mDevCustomInfo)) {
            mInterstitialAd.setDevCustomInfo(mDevCustomInfo);
        }
    }

    private void showInterstitialAd() {
        if (mInterstitialAd == null) {
            appendLog("Load an ad first");
            return;
        }
        boolean success = AdsurgeMediationAdManager.showInterstitialAd(mInterstitialAd, this);
        if (!success) {
            appendLog("Ad not ready, cannot show");
        }
    }

    /**
     * Demonstrates setDevCustomInfo (per-ad custom info) and TANAdSdk.uploadAttributionInfo
     * (one-off attribution reporting). See AdsurgeMediation Guidance §8.
     */
    private void showCustomInfoDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_input, null);
        EditText etAttribution = dialogView.findViewById(R.id.et_input_1);
        EditText etCustomInfo = dialogView.findViewById(R.id.et_input_2);
        etAttribution.setHint("attribution info (JSON)");
        etCustomInfo.setHint("dev custom info (JSON)");

        new AlertDialog.Builder(this)
                .setTitle(R.string.action_custom_info)
                .setView(dialogView)
                .setPositiveButton("Confirm", (dialog, which) -> {
                    String attributionInfo = etAttribution.getText().toString().trim();
                    String devCustomInfo = etCustomInfo.getText().toString().trim();
                    if (!TextUtils.isEmpty(attributionInfo)) {
                        TANAdSdk.uploadAttributionInfo(attributionInfo);
                        appendLog("uploadAttributionInfo: " + attributionInfo);
                    }
                    if (!TextUtils.isEmpty(devCustomInfo)) {
                        mDevCustomInfo = devCustomInfo;
                        if (mInterstitialAd != null) {
                            mInterstitialAd.setDevCustomInfo(devCustomInfo);
                        }
                        appendLog("setDevCustomInfo: " + devCustomInfo);
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.cancel())
                .show();
    }

    private void appendLog(String message) {
        Log.d(TAG, message);
        mLogText.append(message + "\n");
    }
}
