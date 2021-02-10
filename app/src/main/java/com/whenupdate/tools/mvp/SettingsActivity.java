package com.whenupdate.tools.mvp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.whenupdate.tools.BuildConfig;
import com.whenupdate.tools.R;
import com.whenupdate.tools.common.HelpActivity;
import com.whenupdate.tools.common.LicenseActivity;

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

            Preference theme = findPreference("theme");
            if (theme != null) {
                theme.setOnPreferenceChangeListener((preference, newValue) -> {
                    if (getActivity() != null) {
                        getActivity().setResult(1);
                        getActivity().finish();
                        return true;
                    }
                    return false;
                });
            }

            Preference cleanCache = findPreference("btnCleanCache");
            if (cleanCache != null) {
                cleanCache.setOnPreferenceClickListener(preference -> {
                    TaskModel.cleanCache(MainActivity.mContext.getCacheDir());
                    Toast.makeText(getContext(), R.string.cache_cleaned, Toast.LENGTH_SHORT).show();
                    return true;
                });
            }

            Preference version = findPreference("version");
            if (version != null) {
                try {
                    version.setSummary(BuildConfig.VERSION_NAME);
                } catch (Exception ignored) {
                }
            }
            final String appPackageName = "com.whenupdate.tools";
            Preference shareApp = findPreference("share_app");
            if (shareApp != null) {
                shareApp.setOnPreferenceClickListener(preference -> {
                    String recommend = getString(R.string.share_app_recommend);

                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.setType("text/plain");
                    sendIntent.putExtra(Intent.EXTRA_TEXT, recommend + " https://play.google.com/store/apps/details?id=" + appPackageName);
                    startActivity(Intent.createChooser(sendIntent, getString(R.string.share_app_summary)));
                    return true;
                });
            }

            Preference rateApp = findPreference("rate_app");
            if (rateApp != null) {
                rateApp.setOnPreferenceClickListener(preference -> {
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW,
                                Uri.parse("market://details?id=" + appPackageName)));
                    } catch (android.content.ActivityNotFoundException ex) {
                        startActivity(new Intent(Intent.ACTION_VIEW,
                                Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                    }
                    return true;
                });
            }

            Preference licenses = findPreference("open_source_license");
            if (licenses != null) {
                licenses.setOnPreferenceClickListener(preference -> {
                    startActivity(new Intent(getActivity(), LicenseActivity.class));
                    return true;
                });
            }

            Preference faq = findPreference("faq_app");
            if (faq != null) {
                faq.setOnPreferenceClickListener(preference -> {
                    startActivity(new Intent(getActivity(), HelpActivity.class));
                    return true;
                });
            }

            Preference sendEmail = findPreference("send_mail");
            if (sendEmail != null) {
                sendEmail.setOnPreferenceClickListener(preference -> {
                    Intent emailIntent = new Intent(Intent.ACTION_SEND);
                    emailIntent.setType("message/rfc822");
                    emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL,
                            new String[] { getString(R.string.email) });
                    try {
                        startActivity(Intent.createChooser(emailIntent, getString(R.string.send_mail)));
                    } catch (android.content.ActivityNotFoundException ignored) {
                    }
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