package com.whenupdate.tools.common;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.MobileAds;
import com.whenupdate.tools.mvp.MainActivity;

public class StartActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MobileAds.initialize(this, initializationStatus -> {
        });
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
