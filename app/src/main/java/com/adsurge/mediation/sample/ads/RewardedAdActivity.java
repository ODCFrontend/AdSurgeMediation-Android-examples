package com.adsurge.mediation.sample.ads;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.adsurge.mediation.sample.AdsurgeMediationAdManager;
import com.adsurge.mediation.sample.R;
import com.adsurge.mediation.sample.SampleAdConfig;

import com.qq.e.tan.api.ServerSideVerificationOptions;
import com.qq.e.tan.api.TanAd;
import com.qq.e.tan.api.TanRewardVideoAdListener;
import com.qq.e.tan.api.ads.TanRewardedAd;
import com.qq.e.tan.managers.TANAdSdk;
import com.qq.e.tan.util.AdError;

import java.util.Map;

public class RewardedAdActivity extends AppCompatActivity {

    private static final String TAG = "RewardedAdActivity";

    private TanRewardedAd mRewardedAd;
    private TextView mLogText;
    private Button mShowButton;
    private String mDevCustomInfo;

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
        findViewById(R.id.btn_ssv).setOnClickListener(v -> showSsvDialog());
        findViewById(R.id.btn_custom_info).setOnClickListener(v -> showCustomInfoDialog());
    }

    private void loadRewardedAd() {
        appendLog("Loading rewarded ad...");
        mShowButton.setEnabled(false);

        mRewardedAd = AdsurgeMediationAdManager.loadRewardedAd(this, SampleAdConfig.REWARDED_AD_UNIT_ID,
                new TanRewardVideoAdListener() {
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

        if (!TextUtils.isEmpty(mDevCustomInfo)) {
            mRewardedAd.setDevCustomInfo(mDevCustomInfo);
        }
    }

    private void showRewardedAd() {
        if (mRewardedAd == null) {
            appendLog("Load an ad first");
            return;
        }
        boolean success = AdsurgeMediationAdManager.showRewardedAd(mRewardedAd, this);
        if (!success) {
            appendLog("Ad not ready, cannot show");
        }
    }

    /**
     * Demonstrates ServerSideVerificationOptions (SSV), used together with a server-side
     * reward callback to verify reward grants. See AdsurgeMediation Guidance §2.2.
     */
    private void showSsvDialog() {
        if (mRewardedAd == null) {
            Toast.makeText(this, "Please load the advertisement first.", Toast.LENGTH_SHORT).show();
            return;
        }

        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_input, null);
        EditText etUserId = dialogView.findViewById(R.id.et_input_1);
        EditText etCustomData = dialogView.findViewById(R.id.et_input_2);
        etUserId.setHint("user id");
        etCustomData.setHint("custom data");

        new AlertDialog.Builder(this)
                .setTitle(R.string.action_ssv)
                .setView(dialogView)
                .setPositiveButton("Confirm", (dialog, which) -> {
                    ServerSideVerificationOptions options = new ServerSideVerificationOptions.Builder()
                            .setUserId(etUserId.getText().toString())
                            .setCustomData(etCustomData.getText().toString())
                            .build();
                    mRewardedAd.setServerSideVerificationOptions(options);
                    appendLog("SSV params set: userId=" + options.getUserId()
                            + ", customData=" + options.getCustomData());
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.cancel())
                .show();
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
                        if (mRewardedAd != null) {
                            mRewardedAd.setDevCustomInfo(devCustomInfo);
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
