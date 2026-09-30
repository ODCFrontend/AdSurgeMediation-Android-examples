package com.adsurge.mediation.sample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.adsurge.mediation.sample.ads.BannerAdActivity;
import com.adsurge.mediation.sample.ads.InterstitialAdActivity;
import com.adsurge.mediation.sample.ads.RewardedAdActivity;

import com.adsurge.mediation.api.MediationPrivacyConfiguration;
import com.adsurge.mediation.managers.OnStartListener;
import com.adsurge.mediation.managers.AdSurgeMediationSDK;
import com.adsurge.mediation.util.AdError;

public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";

    private TextView mStatusText;

    private boolean mSdkReady = false;
    private String mInitError = null;

    private CheckBox mAgeCheckBox;
    private CheckBox mDoNotSellCheckBox;
    private CheckBox mConsentCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        setupPrivacySettings();
        setupListeners();
    }

    private void initViews() {
        mStatusText = findViewById(R.id.text_sdk_status);
        mAgeCheckBox = findViewById(R.id.checkbox_age_restricted);
        mDoNotSellCheckBox = findViewById(R.id.checkbox_do_not_sell);
        mConsentCheckBox = findViewById(R.id.checkbox_user_consent);
    }

    private void setupListeners() {
        findViewById(R.id.btn_rewarded).setOnClickListener(v ->
                startActivityIfSdkReady(RewardedAdActivity.class));
        findViewById(R.id.btn_interstitial).setOnClickListener(v ->
                startActivityIfSdkReady(InterstitialAdActivity.class));
        findViewById(R.id.btn_banner).setOnClickListener(v ->
                startActivityIfSdkReady(BannerAdActivity.class));
        findViewById(R.id.btn_initialize_sdk).setOnClickListener(v -> initializeSdk());
    }

    private void setupPrivacySettings() {
        mAgeCheckBox.setChecked(false);
        mDoNotSellCheckBox.setChecked(false);
        mConsentCheckBox.setChecked(true);
    }

    private void applyPrivacySettings() {
        MediationPrivacyConfiguration.setAgeRestrictedUser(mAgeCheckBox.isChecked());
        MediationPrivacyConfiguration.setDoNotSell(mDoNotSellCheckBox.isChecked());
        MediationPrivacyConfiguration.setUserConsent(mConsentCheckBox.isChecked());
    }

    private void initializeSdk() {
        mSdkReady = false;
        mInitError = null;
        mStatusText.setText("SDK version: " + AdSurgeMediationSDK.getSdkVersion()
                + "\nInit status: in progress…");

        // Read and apply the current privacy options before each initialization.
        applyPrivacySettings();
        AdSurgeMediationSDK.getInstance().init(this, SampleAdConfig.APP_ID);

        AdSurgeMediationSDK.getInstance().start(new OnStartListener() {
            @Override
            public void onStartComplete() {
                mSdkReady = true;
                Log.d(TAG, "SDK initialized, ready to load ads");
                refreshStatus();
            }

            @Override
            public void onStartFailed(AdError error) {
                mSdkReady = false;
                mInitError = error.errorCode + " - " + error.errorMsg;
                Log.e(TAG, "SDK initialization failed: " + mInitError);
                refreshStatus();
            }
        }, succeededAdnNames -> Log.d(TAG, "ADNs initialized successfully: " + succeededAdnNames));
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshStatus();
    }

    private void refreshStatus() {
        // The init callback may be delivered on a background thread, so post to the UI thread.
        if (!isFinishing()) {
            runOnUiThread(() -> {
                String version = AdSurgeMediationSDK.getSdkVersion();

                StringBuilder sb = new StringBuilder();
                sb.append("SDK version: ").append(version).append('\n');
                if (mSdkReady) {
                    sb.append("Init status: ready ✅");
                } else if (mInitError != null) {
                    sb.append("Init status: failed ❌\n").append(mInitError);
                } else {
                    sb.append("Init status: waiting for privacy settings");
                }
                mStatusText.setText(sb.toString());
            });
        }
    }

    private void startActivityIfSdkReady(Class<?> clazz) {
        if (!mSdkReady) {
            Toast.makeText(this, "SDK is not initialized yet, please try again later", Toast.LENGTH_SHORT).show();
            refreshStatus();
            return;
        }
        startActivity(new Intent(this, clazz));
    }
}
