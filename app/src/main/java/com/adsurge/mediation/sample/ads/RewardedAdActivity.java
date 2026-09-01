package com.adsurge.mediation.sample.ads;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.adsurge.mediation.sample.R;
import com.adsurge.mediation.sample.SampleAdConfig;

import com.qq.e.tan.api.ServerSideVerificationOptions;
import com.qq.e.tan.api.TanAd;
import com.qq.e.tan.api.TanRewardVideoAdListener;
import com.qq.e.tan.api.ads.TanRewardedAd;
import com.qq.e.tan.managers.TANAdSdk;
import com.qq.e.tan.util.AdError;

import java.util.Map;

public class RewardedAdActivity extends Activity {

    private static final String TAG = "RewardedAdActivity";

    private TanRewardedAd mRewardedAd;
    private TextView mLogText;
    private Button mShowButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rewarded_ad);

        initViews();
        setupListeners();
    }

    private void initViews() {
        mLogText = findViewById(R.id.text_log);
        mShowButton = findViewById(R.id.btn_show);
        mShowButton.setEnabled(false);
    }

    private void setupListeners() {
        findViewById(R.id.btn_load).setOnClickListener(v -> loadRewardedAd());
        mShowButton.setOnClickListener(v -> showRewardedAd());
    }

    private void loadRewardedAd() {
        appendLog("Loading rewarded ad...");
        mShowButton.setEnabled(false);

        mRewardedAd = new TanRewardedAd(this, SampleAdConfig.REWARDED_AD_UNIT_ID);
        mRewardedAd.setListener(new TanRewardVideoAdListener() {
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
                    public void onReward(TanAd tanAd, Map<String, Object> rewardInfo) {
                        appendLog("Reward granted: " + rewardInfo);
                        Toast.makeText(RewardedAdActivity.this, "Reward granted!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onRewardFailed(TanAd tanAd, int errorCode, String errorMsg) {
                        appendLog("Reward failed: " + errorCode + " - " + errorMsg);
                    }

                    @Override
                    public void onAdClosed(TanAd tanAd) {
                        appendLog("Ad closed");
                        mShowButton.setEnabled(false);
                    }
                });

        // Demonstrates ServerSideVerificationOptions (SSV), used together with a server-side
        // reward callback to verify reward grants. See AdsurgeMediation Guidance §2.2.
        // Replace userId/customData with real values in production.
        mRewardedAd.setServerSideVerificationOptions(new ServerSideVerificationOptions.Builder()
                .setUserId("demo_user_id")
                .setCustomData("demo_custom_data")
                .build());

        // Demonstrates setDevCustomInfo (per-ad custom info) and TANAdSdk.uploadAttributionInfo
        // (one-off attribution reporting). See AdsurgeMediation Guidance §8.
        TANAdSdk.uploadAttributionInfo("demo_attribution_info");
        mRewardedAd.setDevCustomInfo("demo_custom_info");

        mRewardedAd.loadAd();
    }

    private void showRewardedAd() {
        if (mRewardedAd == null) {
            appendLog("Load an ad first");
            return;
        }
        if (!mRewardedAd.isValid()) {
            appendLog("Ad not ready, call loadAd() again");
            return;
        }
        mRewardedAd.showAd(this);
    }

    private void appendLog(String message) {
        Log.d(TAG, message);
        mLogText.append(message + "\n");
    }
}
