package com.whenupdate.tools.mvp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.webkit.WebSettingsCompat;
import androidx.webkit.WebViewFeature;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.whenupdate.tools.R;

public class ViewActivity extends AppCompatActivity {
    public static final String LINK_KEY = "link";
    private String link;
    private WebView webView;
    private String currentUrl;
    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TaskModel.setNewTheme(this);
        setContentView(R.layout.activity_view);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        init();
        initAd();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_view_task, menu);
        return true;
    }

    private void initAd() {
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-8072450081468494/4654845202");
        AdRequest request = new AdRequest.Builder().build();
        mInterstitialAd.loadAd(request);

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                onBackPressed();
            }
        });
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void init() {
        ProgressBar progressBar = findViewById(R.id.progressBar_horizontal);
        webView = (WebView) findViewById(R.id.webView);
        WebSettings webSettings = webView.getSettings();

        webSettings.setJavaScriptEnabled(true); //включение javascript
        webSettings.setAllowFileAccess(false); //доступ к файловой системе
        webSettings.setBuiltInZoomControls(true); //включить зум
        webSettings.setSupportZoom(true);
        webSettings.setDisplayZoomControls(false);

        SimpleWebViewClient webViewClient = new SimpleWebViewClient(this, progressBar);
        webView.setWebViewClient(webViewClient);

        if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            String theme = prefs.getString("theme", "light");
            if (theme.equals("dark")) {
                WebSettingsCompat.setForceDark(webSettings, WebSettingsCompat.FORCE_DARK_ON);
            } else {
                WebSettingsCompat.setForceDark(webSettings, WebSettingsCompat.FORCE_DARK_OFF);
            }
        }

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            link = extras.getString(LINK_KEY);
            currentUrl = link;
            webView.loadUrl(link);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && this.webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                } else super.onBackPressed();
                return true;
            case R.id.itemShare:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                if (currentUrl != null) {
                    sendIntent.putExtra(Intent.EXTRA_TEXT, currentUrl);
                    sendIntent.setType("text/plain");
                    startActivity(Intent.createChooser(sendIntent, getString(R.string.share_title)));
                }

                return true;
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            TaskModel.cleanCache(getCacheDir());
        } catch (Exception ignored) {
        }
    }

    private class SimpleWebViewClient extends WebViewClient {
        private Activity activity;
        private ProgressBar progressBar;

        SimpleWebViewClient(Activity activity, ProgressBar progressBar) {
            this.activity = activity;
            this.progressBar = progressBar;
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView webView, String url) {
            if (link != null) {
                int indexProtocol = ((link.contains("https")) ? 8 : 7);
                int index = link.indexOf('/', indexProtocol);

                String serverFull = link.substring(0, (index == -1) ? link.length() : index);
                String server = serverFull.substring(indexProtocol, serverFull.lastIndexOf('.'))
                        .replace("www.", "");

                if (url.contains(server)) {
                    currentUrl = url;
                    webView.loadUrl(url);
                    return false;
                }
            }

            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                activity.startActivity(intent);
                return true;
            } catch (Exception ignored) {
            }
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            progressBar.setVisibility(View.GONE);
            view.setVisibility(View.VISIBLE);
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            progressBar.setVisibility(View.GONE);
        }
    }
}