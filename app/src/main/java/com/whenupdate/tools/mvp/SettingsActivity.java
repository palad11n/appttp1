package com.whenupdate.tools.mvp;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.whenupdate.tools.R;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TaskModel.setNewTheme(this);
        setContentView(R.layout.settings_activity);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment.newInstance())
                .commit();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {

        static SettingsFragment newInstance() {
            //Bundle args = new Bundle();
            SettingsFragment fragment = new SettingsFragment();
            return fragment;
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
            findPreference("theme").setOnPreferenceChangeListener((preference, newValue) -> {
                if (getActivity() != null){
                    getActivity().setResult(1);
                    getActivity().finish();
                    return true;
                }
               return false;
            });

            Preference cleanCache = findPreference("btnCleanCache");
            if (cleanCache != null) {
                cleanCache.setOnPreferenceClickListener(preference -> {
                    TaskModel.cleanCache(MainActivity.mContext.getCacheDir());
                    Toast.makeText(getContext(), R.string.cache_cleaned, Toast.LENGTH_SHORT).show();
                    return true;
                });
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}