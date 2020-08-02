package com.whenupdate.tools.common;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.whenupdate.tools.mvp.MainActivity;
import com.whenupdate.tools.mvp.TaskModel;

public class StartActivity extends AppCompatActivity {
    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MobileAds.initialize(this, initializationStatus -> {
        });
        initAd();
    }

    private void initAd() {
        if (!TaskModel.isNetworkAvailable(this)) {
            startMain();
            return;
        }

        mInterstitialAd = new InterstitialAd(this);
        //testing: ca-app-pub-3940256099942544/1033173712
        mInterstitialAd.setAdUnitId("ca-app-pub-8072450081468494/4654845202");
        AdRequest request = new AdRequest.Builder().build();
        mInterstitialAd.loadAd(request);
        if (mInterstitialAd != null) {
            mInterstitialAd.setAdListener(new AdListener() {

                @Override
                public void onAdFailedToLoad(LoadAdError loadAdError) {
                    startMain();
                }

                @Override
                public void onAdLoaded() {
                    mInterstitialAd.show();
                }

                @Override
                public void onAdClosed() {
                    startMain();
                }
            });
        }
    }

    private void startMain() {
        Intent intent = new Intent(StartActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
