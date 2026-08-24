package com.adsurge.mediation.sample.ads;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.adsurge.mediation.sample.AdsurgeMediationAdManager;
import com.adsurge.mediation.sample.R;
import com.adsurge.mediation.sample.SampleAdConfig;

import com.qq.e.tan.api.TanAd;
import com.qq.e.tan.api.TanAdViewAdListener;
import com.qq.e.tan.api.ads.TanAdView;
import com.qq.e.tan.managers.TANAdSdk;
import com.qq.e.tan.util.AdError;

/**
 * Demonstrates Banner (320x50) and MREC (300x250) ad formats, plus how to report
 * developer custom info / attribution info (see AdsurgeMediation Guidance §8).
 */
public class BannerAdActivity extends AppCompatActivity {

    private static final String TAG = "BannerAdActivity";

    private TanAdView mAdView;
    private TextView mLogText;
    private FrameLayout mContainer;
    private RadioGroup mRgFormat;
    private boolean mIsMrec;
    private String mDevCustomInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banner_ad);

        initViews();
        setupListeners();
        loadAd();
    }

    private void initViews() {
        mLogText = findViewById(R.id.text_log);
        mContainer = findViewById(R.id.banner_container);
        mRgFormat = findViewById(R.id.rg_format);
    }

    private void setupListeners() {
        mRgFormat.setOnCheckedChangeListener((group, checkedId) -> {
            mIsMrec = checkedId == R.id.rb_mrec;
            loadAd();
        });
        findViewById(R.id.btn_custom_info).setOnClickListener(v -> showCustomInfoDialog());
    }

    private void loadAd() {
        // Destroy the previous ad before switching format / reloading.
        if (mAdView != null) {
            mAdView.destroy();
            mContainer.removeAllViews();
        }

        String label = mIsMrec ? "MREC" : "Banner";
        appendLog("Loading " + label + " ad...");

        TanAdViewAdListener listener = new TanAdViewAdListener() {
            @Override
            public void onAdLoaded(TanAd tanAd) {
                appendLog(label + " loaded");
            }

            @Override
            public void onAdDisplayed(TanAd tanAd) {
                appendLog(label + " displayed");
            }

            @Override
            public void onAdClicked(TanAd tanAd) {
                appendLog(label + " clicked");
            }

            @Override
            public void onAdLoadFailed(TanAd tanAd, AdError error) {
                appendLog(label + " load failed: " + error.errorCode + " - " + error.errorMsg);
            }

            @Override
            public void onAdShowFailed(TanAd tanAd, AdError error) {
                appendLog(label + " show failed: " + error.errorCode + " - " + error.errorMsg);
            }
        };

        mAdView = mIsMrec
                ? AdsurgeMediationAdManager.loadMrecAd(this, SampleAdConfig.MREC_AD_UNIT_ID, mContainer, listener)
                : AdsurgeMediationAdManager.loadBannerAd(this, SampleAdConfig.BANNER_AD_UNIT_ID, mContainer, listener);

        if (!TextUtils.isEmpty(mDevCustomInfo)) {
            mAdView.setDevCustomInfo(mDevCustomInfo);
        }
    }

    /**
     * Demonstrates setDevCustomInfo (per-ad custom info, reported with every impression/click)
     * and TANAdSdk.uploadAttributionInfo (one-off attribution info reporting).
     * See AdsurgeMediation Guidance §8.
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
                        if (mAdView != null) {
                            mAdView.setDevCustomInfo(devCustomInfo);
                        }
                        appendLog("setDevCustomInfo: " + devCustomInfo);
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.cancel())
                .show();
    }

    @Override
    protected void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }

    private void appendLog(String message) {
        Log.d(TAG, message);
        mLogText.append(message + "\n");
    }
}
