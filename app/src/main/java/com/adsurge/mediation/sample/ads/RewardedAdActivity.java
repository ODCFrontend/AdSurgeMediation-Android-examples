package com.adsurge.mediation.sample.ads;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.adsurge.mediation.sample.R;
import com.adsurge.mediation.sample.SampleAdConfig;

import com.adsurge.mediation.api.ServerSideVerificationOptions;
import com.adsurge.mediation.api.MediationAd;
import com.adsurge.mediation.api.MediationRewardVideoAdListener;
import com.adsurge.mediation.api.ads.MediationRewardedAd;
import com.adsurge.mediation.managers.AdSurgeMediationSDK;
import com.adsurge.mediation.util.AdError;

import java.util.Map;

public class RewardedAdActivity extends Activity {

    private static final String TAG = "RewardedAdActivity";

    private MediationRewardedAd mRewardedAd;
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

        mRewardedAd = new MediationRewardedAd(this, SampleAdConfig.REWARDED_AD_UNIT_ID);
        mRewardedAd.setListener(new MediationRewardVideoAdListener() {
                    @Override
                    public void onAdLoaded(MediationAd mediationAd) {
                        appendLog("Ad loaded");
                        // Demonstrates the ad revenue detail fields added in AdsurgeMediation
                        // SDK 1.8.0. See AdsurgeMediation Guidance §3.1.
                        appendLog("Ad source unit ID: " + mediationAd.getAdSourceUnitID());
                        appendLog("Currency: " + mediationAd.getCurrency());
                        appendLog("Revenue precision: " + mediationAd.getRevenuePrecision());
                        appendLog("Bidding type: " + mediationAd.getBiddingType());
                        appendLog("Region code: " + mediationAd.getRegionCode());
                        appendLog("Mediation placement ID: " + mediationAd.getMediationPlacementID());
                        mShowButton.setEnabled(true);
                    }

                    @Override
                    public void onAdDisplayed(MediationAd mediationAd) {
                        appendLog("Ad displayed");
                    }

                    @Override
                    public void onAdClicked(MediationAd mediationAd) {
                        appendLog("Ad clicked");
                    }

                    @Override
                    public void onAdLoadFailed(MediationAd mediationAd, AdError error) {
                        appendLog("Load failed: " + error.errorCode + " - " + error.errorMsg);
                    }

                    @Override
                    public void onAdShowFailed(MediationAd mediationAd, AdError error) {
                        appendLog("Show failed: " + error.errorCode + " - " + error.errorMsg);
                    }

                    @Override
                    public void onReward(MediationAd mediationAd, Map<String, Object> rewardInfo) {
                        appendLog("Reward granted: " + rewardInfo);
                        Toast.makeText(RewardedAdActivity.this, "Reward granted!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onRewardFailed(MediationAd mediationAd, int errorCode, String errorMsg) {
                        appendLog("Reward failed: " + errorCode + " - " + errorMsg);
                    }

                    @Override
                    public void onAdClosed(MediationAd mediationAd) {
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

        // Demonstrates setDevCustomInfo (per-ad custom info) and AdSurgeMediationSDK.uploadAttributionInfo
        // (one-off attribution reporting). See AdsurgeMediation Guidance §8.
        AdSurgeMediationSDK.uploadAttributionInfo("demo_attribution_info");
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
