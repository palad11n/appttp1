package com.whenupdate.tools.common;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.whenupdate.tools.R;
import com.whenupdate.tools.mvp.TaskModel;

import java.util.ArrayList;
import java.util.List;

public class LicenseActivity extends AppCompatActivity {

    static final List<License> LICENSES;

    static {
        LICENSES = new ArrayList<>();
        LICENSES.add(new License("Material Components for Android", "https://github.com/material-components/material-components-android/blob/master/LICENSE"));
        LICENSES.add(new License("Gson", "https://github.com/google/gson/blob/master/LICENSE"));
        LICENSES.add(new License("Jsoup", "https://jsoup.org/license"));
        LICENSES.add(new License("Swiperefreshlayout", "https://developer.android.com/jetpack/androidx/releases/swiperefreshlayout?hl=ru"));
        LICENSES.add(new License("RxJava", "https://github.com/ReactiveX/RxJava/blob/3.x/LICENSE"));
        LICENSES.add(new License("RxAndroid", "https://github.com/ReactiveX/RxAndroid/blob/3.x/LICENSE"));
        LICENSES.add(new License("AndroidSwipeLayout", "https://github.com/daimajia/AndroidSwipeLayout/blob/master/LICENSE"));
        LICENSES.add(new License("Mobile Ads SDK (Android)", "https://developers.google.com/admob/android/quick-start"));
        LICENSES.add(new License("firebase-core", "https://firebase.google.com/docs/android/setup#available-libraries"));
        LICENSES.add(new License("firebase-analytics", "https://firebase.google.com/docs/analytics/get-started?platform=android"));
        LICENSES.add(new License("Appcompat", "https://developer.android.com/jetpack/androidx/releases/appcompat"));
        LICENSES.add(new License("material-design-icons", "https://github.com/google/material-design-icons/blob/master/LICENSE"));
        LICENSES.add(new License("Android Support RecyclerView v7", "https://developer.android.com/topic/libraries/support-library"));
        LICENSES.add(new License("Android Support CardView v7", "https://developer.android.com/topic/libraries/support-library"));
        LICENSES.add(new License("AndroidX Preference", "https://developer.android.com/jetpack/androidx/releases/preference"));
        LICENSES.add(new License("Android Multidex Support Library", "https://developer.android.com/topic/libraries/support-library"));
        LICENSES.add(new License("Picasso", "https://square.github.io/picasso"));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TaskModel.setNewTheme(this);
        setContentView(R.layout.activity_license);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.license_title);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
        init();
    }

    private void init() {
        ListView listView = (ListView) findViewById(R.id.list_license);
        ArrayAdapter<License> adapter = new LicensesAdapter(this);
        listView.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private static class License {
        public final String name;
        public final String link;

        License(String name, String link) {
            this.name = name;
            this.link = link;
        }
    }

    private class LicensesAdapter extends ArrayAdapter<License> {
        LicensesAdapter(@NonNull Context context) {
            super(context, R.layout.item_license, LICENSES);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            License license = getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext())
                        .inflate(R.layout.item_license, null);
            }

            TextView nameLicenseText = (TextView) convertView.findViewById(R.id.name_license);
            nameLicenseText.setText(license.name);
            nameLicenseText.setClickable(true);
            nameLicenseText.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse(license.link))));
            return convertView;
        }
    }
}
