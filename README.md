# AdSurgeMediation Android Sample (Java)

An official sample project for developers, demonstrating how to integrate the AdSurgeMediation SDK (formerly known as TAN, Serafino) and call its ad formats.

## Features

- SDK initialization (`MainActivity`)
- Privacy compliance configuration (COPPA / CCPA / GDPR, on `MainActivity`)
- Rewarded video (`RewardedAdActivity`), including Server-Side Verification (SSV) params
- Interstitial (`InterstitialAdActivity`)
- Banner (320x50) / MREC (300x250) with runtime format switch (`BannerAdActivity`)
- Developer custom info reporting (`setDevCustomInfo`) and attribution info reporting
  (`TANAdSdk.uploadAttributionInfo`) on every ad format
- Integrated with AdMob as a sample ADN

## Project Structure

```text
app/src/main/java/com/adsurge/mediation/sample/
├── SampleAdConfig.java             App ID and ad unit ID configuration
├── MainActivity.java               Home screen: SDK initialization + privacy settings
└── ads/                            Ad format sample Activities
    ├── RewardedAdActivity.java
    ├── InterstitialAdActivity.java
    └── BannerAdActivity.java
```

## Dependency Setup

AdSurgeMediation artifacts are hosted on GitHub Packages. You need a GitHub account with read access to the repository plus a Personal Access Token.
You can obtain these from the platform: https://www.adsurge.com/mediation/sdk_download

## Configure Ad Parameters

Fill in your own App ID and ad unit IDs (obtained from the AdSurgeMediation dashboard) in `SampleAdConfig.java`:

```java
public static final String APP_ID = "...";
public static final String REWARDED_AD_UNIT_ID = "...";
public static final String INTERSTITIAL_AD_UNIT_ID = "...";
public static final String BANNER_AD_UNIT_ID = "...";
public static final String MREC_AD_UNIT_ID = "...";
```
