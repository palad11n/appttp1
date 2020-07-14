package com.whenupdate.tools.mvp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.whenupdate.tools.R;

public class ViewActivity extends AppCompatActivity {
    public static final String LINK_KEY = "link";
    private String link;
    private WebView webView;
    private String currentUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        init();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_view_task, menu);
        return true;
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void init() {
        ProgressBar progressBar = findViewById(R.id.progressBar_horizontal);
        webView = (WebView) findViewById(R.id.webView);
        SimpleWebViewClient webViewClient = new SimpleWebViewClient(this, progressBar);
        webView.setWebViewClient(webViewClient);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            link = extras.getString(LINK_KEY);
            currentUrl = link;
            webView.getSettings().setJavaScriptEnabled(true);
            webView.loadUrl(link);
        }
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
                super.onBackPressed();
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
                int index = link.indexOf('/', ((link.contains("https")) ? 8 : 7));
                String serverFull = link.substring(0, (index == -1) ? link.length() : index);
                String server = serverFull.substring(0, serverFull.lastIndexOf('.'));

                if (url.contains(server)) {
                    currentUrl = url;
                    webView.loadUrl(url);
                    return false;
                }
            }
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            activity.startActivity(intent);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            progressBar.setVisibility(View.GONE);
            view.setVisibility(View.VISIBLE);
        }
    }
}
