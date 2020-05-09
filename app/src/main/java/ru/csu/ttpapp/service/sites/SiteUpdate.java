package ru.csu.ttpapp.service.sites;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.widget.Toast;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import ru.csu.ttpapp.mvp.MainActivity;
import ru.csu.ttpapp.service.parsers.FindAnimeParsingThread;
import ru.csu.ttpapp.service.parsers.ParsingSoundCloudThread;
import ru.csu.ttpapp.service.parsers.SerialMovieParsingThread;
import ru.csu.ttpapp.service.parsers.TitleParsingThread;

public class SiteUpdate implements ISite {
    private final String linkUsers;
    private InfoOfSite infoOfSite =  new InfoOfSite();
    private String TAG_CLASS;
    private static final Map<String, String> map;

    static {
        map = new HashMap<>();
        map.put("//soundcloud.com/", "time");
        map.put("seria", ".epscape_tr");
        map.put("//findanime.me/", ".table td.hidden-xxs");
    }

    public SiteUpdate(String site) {
        linkUsers = site;
        for (String i : map.keySet()) {
            if (site.contains(i)) {
                TAG_CLASS = map.get(i);
                break;
            }
        }
    }

    @Override
    public Date findUpDate() {
        if (!isNetworkAvailable()){
            MainActivity.presenter.getNotConnection();
            return null;
        }
        parsingDate();
        return infoOfSite.getDate();
    }

    @Override
    public String getTitleSite() {
        if (!isNetworkAvailable()){
            MainActivity.presenter.getNotConnection();
            return null;
        }
        parsingTitle();
        return infoOfSite.getTitle();
    }

    private void parsingDate() {
        AsyncTask<String, Void, String> pt;
        if (linkUsers.contains("//soundcloud.com/")) {
            pt = new ParsingSoundCloudThread();
        } else if (linkUsers.contains("seria")) {
            pt = new SerialMovieParsingThread();
        } else {
            pt = new FindAnimeParsingThread();
            TAG_CLASS = ".table td.hidden-xxs";
        }
        pt.execute(linkUsers, TAG_CLASS);
        try {
            String date = pt.get();
            if (date.equals("")) {
                Toast.makeText(MainActivity.mContext, "Site is RIP!", Toast.LENGTH_SHORT).show();
                return;
            }
            infoOfSite.setDate(date);
        } catch (Exception e) {
            //ignore
        }
    }

    private void parsingTitle() {
        TitleParsingThread pt = new TitleParsingThread();
        pt.execute(linkUsers);
        try {
            infoOfSite.setTitle(pt.get());
        } catch (Exception e) {
            //ignore
        }
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) MainActivity.mContext.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
                if (capabilities != null) {
                    if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                            || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                            || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
                    )
                        return true;
                }
            } else {
                try {
                    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                    if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
                        return true;
                    }
                } catch (Exception e) {
                }
            }
        }
        return false;
    }
}
