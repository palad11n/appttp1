package com.whenupdate.tools.mvp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.whenupdate.tools.R;

@SuppressWarnings("all")
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public final static String DATABASE = "list_db";
    public static Context mContext;

    private ProgressDialog progressDialog;
    private BottomNavigationView bottomNav;
    private NavigationView navView;

    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TaskModel.setNewTheme(this);
        setContentView(R.layout.activity_main);
        initNav();
        init();
    }

    private void initNav() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getText(R.string.home));
        navView = findViewById(R.id.nav_view);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        mDrawerLayout = findViewById(R.id.cl_main);
        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                findViewById(R.id.cl_main),
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );

        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        navView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawers();
            return;
        }

        super.onBackPressed();
    }

    private void init() {
        if (mContext == null)
            mContext = MainActivity.this;
        //initBottomNavigation();

        TaskModel taskModel = new TaskModel(this, FavoritesFragment.DATABASE);
        taskModel.loadTasks(listTasks -> {
            //if (listTasks.size() > 0) {
            //      bottomNav.getOrCreateBadge(R.id.itemFavorites).setVisible(true);
            // } else bottomNav.getOrCreateBadge(R.id.itemFavorites).setVisible(false);
        });
    }

//    private void initBottomNavigation() {
//        // bottomNav = findViewById(R.id.bottom_nav);
//        bottomNav.setOnNavigationItemSelectedListener(item -> {
//            Fragment selectedFragment = null;
//            switch (item.getItemId()) {
//                case R.id.itemHome:
//                    selectedFragment = new HomeFragment();
//                    break;
//                case R.id.itemFavorites:
//                    selectedFragment = new FavoritesFragment();
//                    break;
//                case R.id.itemPreferences:
//                    Intent intent = new Intent(this, SettingsActivity.class);
//                    startActivityForResult(intent, 1);
//                    break;
//            }
//            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
//                    selectedFragment).commit();
//            return true;
//        });
//    }

    @Override
    protected void onResume() {
        super.onResume();
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getText(R.string.home));
        navView.getMenu().getItem(0).setChecked(true);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                FavoritesFragment.getInstance()).commit();
        // bottomNav.setSelectedItemId(R.id.itemHome);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                HomeFragment.getInstance()).commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == 1) {
            recreate();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        switch (item.getItemId()) {
            case R.id.itemAbout:
                showDialogPref(R.layout.dialog_about);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDialogPref(int resId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(resId, null);
        builder.setView(view)
                .setPositiveButton(R.string.cancel, (dialog, id) -> dialog.dismiss());
        builder.create();
        builder.show();
    }

    public void isUpdate(boolean isExistUpdate) {
        if (isExistUpdate)
            showToast(getString(R.string.update_exist));
        else showToast(getString(R.string.update_not), R.drawable.ic_sentiment_dissatisfied_toast);
    }

    public void alertConnection() {
        showToast(getString(R.string.check_internet), R.drawable.ic_wifi_off_24px);
    }

    public void showToastSaveFailed() {
        showToast(getString(R.string.fail_save),
                R.drawable.ic_sentiment_dissatisfied_toast);
    }

    public void showToastSiteRip(String link) {
        showToast(getString(R.string.site_rip) + link,
                R.drawable.ic_sentiment_dissatisfied_toast);
    }

    public void showToast(String textToast) {
        showToast(textToast, R.drawable.ic_sentiment_smale_toast);
    }

    public void showToast(String textToast, int resIdIcon) {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast_layout, (ViewGroup) findViewById(R.id.toast_root));
        TextView textView = layout.findViewById(R.id.toast_text);
        ImageView imageView = layout.findViewById(R.id.toast_icon);

        textView.setText(textToast);
        imageView.setImageResource(resIdIcon);

        Toast toast = new Toast(this);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setView(layout);
        toast.show();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment selectedFragment = null;
        ActionBar actionBar = getSupportActionBar();
        switch (item.getItemId()) {
            case R.id.itemHome:
                actionBar.setTitle(getText(R.string.home));
                selectedFragment = HomeFragment.getInstance();
                mDrawerLayout.closeDrawers();
                break;
            case R.id.itemFavorites:
                mDrawerLayout.closeDrawers();
                actionBar.setTitle(getText(R.string.postponed));
                selectedFragment = FavoritesFragment.getInstance();
                break;
            case R.id.itemPreferences:
                mDrawerLayout.closeDrawers();
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivityForResult(intent, 1);
                return true;
            case R.id.itemAbout:
                mDrawerLayout.closeDrawers();
                showDialogPref(R.layout.dialog_about);
                return true;
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                selectedFragment).commit();
        return true;
    }
}