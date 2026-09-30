package com.adsurge.mediation.sample.ads;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.adsurge.mediation.sample.R;
import com.adsurge.mediation.sample.SampleAdConfig;

import com.adsurge.mediation.api.MediationAd;
import com.adsurge.mediation.api.MediationAdFormat;
import com.adsurge.mediation.api.MediationAdViewAdListener;
import com.adsurge.mediation.api.ads.MediationAdView;
import com.adsurge.mediation.managers.AdSurgeMediationSDK;
import com.adsurge.mediation.util.AdError;

public class BannerAdActivity extends Activity {

    private static final String TAG = "BannerAdActivity";

    private MediationAdView mAdView;
    private TextView mLogText;
    private FrameLayout mContainer;
    private RadioGroup mRgFormat;
    private boolean mIsMrec;

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
    }

    private void loadAd() {
        // Destroy the previous ad before switching format / reloading.
        if (mAdView != null) {
            mAdView.destroy();
            mContainer.removeAllViews();
        }

        String label = mIsMrec ? "MREC" : "Banner";
        appendLog("Loading " + label + " ad...");

        MediationAdViewAdListener listener = new MediationAdViewAdListener() {
            @Override
            public void onAdLoaded(MediationAd mediationAd) {
                appendLog(label + " loaded");
            }

            @Override
            public void onAdDisplayed(MediationAd mediationAd) {
                appendLog(label + " displayed");
            }

            @Override
            public void onAdClicked(MediationAd mediationAd) {
                appendLog(label + " clicked");
            }

            @Override
            public void onAdLoadFailed(MediationAd mediationAd, AdError error) {
                appendLog(label + " load failed: " + error.errorCode + " - " + error.errorMsg);
            }

            @Override
            public void onAdShowFailed(MediationAd mediationAd, AdError error) {
                appendLog(label + " show failed: " + error.errorCode + " - " + error.errorMsg);
            }
        };

        if (mIsMrec) {
            mAdView = new MediationAdView(this, SampleAdConfig.MREC_AD_UNIT_ID, MediationAdFormat.MREC);
        } else {
            mAdView = new MediationAdView(this, SampleAdConfig.BANNER_AD_UNIT_ID);
        }
        mAdView.setListener(listener);

        int widthDp = mIsMrec ? 300 : 320;
        int heightDp = mIsMrec ? 250 : 50;
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                dpToPx(widthDp), dpToPx(heightDp));
        params.gravity = Gravity.CENTER;
        mContainer.addView(mAdView, params);

        // Demonstrates setDevCustomInfo (per-ad custom info, reported with every
        // impression/click) and AdSurgeMediationSDK.uploadAttributionInfo (one-off attribution).
        // See AdsurgeMediation Guidance §8. Replace with real values in production.
        AdSurgeMediationSDK.uploadAttributionInfo("demo_attribution_info");
        mAdView.setDevCustomInfo("demo_custom_info");

        mAdView.loadAd();
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density + 0.5f);
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
