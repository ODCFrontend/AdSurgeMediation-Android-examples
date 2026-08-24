package com.adsurge.mediation.sample;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.adsurge.mediation.sample.ads.BannerAdActivity;
import com.adsurge.mediation.sample.ads.InterstitialAdActivity;
import com.adsurge.mediation.sample.ads.RewardedAdActivity;
import com.adsurge.mediation.sample.privacy.PrivacySettingsActivity;

import com.qq.e.tan.managers.TANAdSdk;

public class MainActivity extends AppCompatActivity {

    private TextView mStatusText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        setupListeners();
    }

    private void initViews() {
        mStatusText = findViewById(R.id.text_sdk_status);
    }

    private void setupListeners() {
        findViewById(R.id.btn_rewarded).setOnClickListener(v ->
                startActivityIfSdkReady(RewardedAdActivity.class));
        findViewById(R.id.btn_interstitial).setOnClickListener(v ->
                startActivityIfSdkReady(InterstitialAdActivity.class));
        findViewById(R.id.btn_banner).setOnClickListener(v ->
                startActivityIfSdkReady(BannerAdActivity.class));
        findViewById(R.id.btn_privacy).setOnClickListener(v ->
                startActivity(new Intent(this, PrivacySettingsActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshStatus();
    }

    private void refreshStatus() {
        String version = TANAdSdk.getSdkVersion();
        boolean ready = SampleApplication.isSdkReady();
        String error = SampleApplication.getInitError();

        StringBuilder sb = new StringBuilder();
        sb.append("SDK version: ").append(version).append('\n');
        if (ready) {
            sb.append("Init status: ready ✅");
        } else if (error != null) {
            sb.append("Init status: failed ❌\n").append(error);
        } else {
            sb.append("Init status: in progress…");
        }
        mStatusText.setText(sb.toString());
    }

    private void startActivityIfSdkReady(Class<?> clazz) {
        if (!SampleApplication.isSdkReady()) {
            Toast.makeText(this, "SDK is not initialized yet, please try again later", Toast.LENGTH_SHORT).show();
            refreshStatus();
            return;
        }
        startActivity(new Intent(this, clazz));
    }
}
