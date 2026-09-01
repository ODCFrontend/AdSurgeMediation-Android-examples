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

import com.qq.e.tan.api.TanAd;
import com.qq.e.tan.api.TanAdFormat;
import com.qq.e.tan.api.TanAdViewAdListener;
import com.qq.e.tan.api.ads.TanAdView;
import com.qq.e.tan.managers.TANAdSdk;
import com.qq.e.tan.util.AdError;

public class BannerAdActivity extends Activity {

    private static final String TAG = "BannerAdActivity";

    private TanAdView mAdView;
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

        if (mIsMrec) {
            mAdView = new TanAdView(this, SampleAdConfig.MREC_AD_UNIT_ID, TanAdFormat.MREC);
        } else {
            mAdView = new TanAdView(this, SampleAdConfig.BANNER_AD_UNIT_ID);
        }
        mAdView.setListener(listener);

        int widthDp = mIsMrec ? 300 : 320;
        int heightDp = mIsMrec ? 250 : 50;
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                dpToPx(widthDp), dpToPx(heightDp));
        params.gravity = Gravity.CENTER;
        mContainer.addView(mAdView, params);

        // Demonstrates setDevCustomInfo (per-ad custom info, reported with every
        // impression/click) and TANAdSdk.uploadAttributionInfo (one-off attribution).
        // See AdsurgeMediation Guidance §8. Replace with real values in production.
        TANAdSdk.uploadAttributionInfo("demo_attribution_info");
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
