package com.adsurge.mediation.sample.privacy;

import android.os.Bundle;
import android.widget.CheckBox;

import androidx.appcompat.app.AppCompatActivity;

import com.adsurge.mediation.sample.R;
import com.qq.e.tan.api.TANPrivacyConfiguration;

public class PrivacySettingsActivity extends AppCompatActivity {

    private CheckBox mAgeCheckBox;
    private CheckBox mDoNotSellCheckBox;
    private CheckBox mConsentCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_settings);

        initViews();
        bindCurrentValues();
        setupListeners();
    }

    private void initViews() {
        mAgeCheckBox = findViewById(R.id.checkbox_age_restricted);
        mDoNotSellCheckBox = findViewById(R.id.checkbox_do_not_sell);
        mConsentCheckBox = findViewById(R.id.checkbox_user_consent);
    }

    private void bindCurrentValues() {
        mAgeCheckBox.setChecked(TANPrivacyConfiguration.isAgeRestrictedUser());
        mDoNotSellCheckBox.setChecked(TANPrivacyConfiguration.isDoNotSell());
        mConsentCheckBox.setChecked(TANPrivacyConfiguration.hasUserConsent());
    }

    private void setupListeners() {
        mAgeCheckBox.setOnCheckedChangeListener((buttonView, isChecked) ->
                TANPrivacyConfiguration.setAgeRestrictedUser(isChecked));
        mDoNotSellCheckBox.setOnCheckedChangeListener((buttonView, isChecked) ->
                TANPrivacyConfiguration.setDoNotSell(isChecked));
        mConsentCheckBox.setOnCheckedChangeListener((buttonView, isChecked) ->
                TANPrivacyConfiguration.setUserConsent(isChecked));
    }
}
