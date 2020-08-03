package com.whenupdate.tools.common;

import android.content.Intent;
import android.content.SharedPreferences;
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
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        MobileAds.initialize(this, initializationStatus -> {
        });
        initAd();
    }

    private void initAd() {
        mInterstitialAd = new InterstitialAd(this);
        //testing: ca-app-pub-3940256099942544/1033173712
        mInterstitialAd.setAdUnitId("ca-app-pub-8072450081468494/4654845202");
    }

    private void startMain() {
        Intent intent = new Intent(StartActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (prefs.getBoolean("first_start", true)) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("first_start", false).apply();
            editor.apply();
            editor.commit();

            startMain();
        } else {
            if (TaskModel.isNetworkAvailable(this) && mInterstitialAd != null) {
                AdRequest request = new AdRequest.Builder().build();
                mInterstitialAd.loadAd(request);
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
            } else {
                startMain();
            }
        }
    }
}
