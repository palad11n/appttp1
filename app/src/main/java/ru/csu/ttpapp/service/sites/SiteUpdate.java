package ru.csu.ttpapp.service.sites;

import android.os.AsyncTask;
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
        map.put("//findanime.me/", ".table");
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
        parsingDate();
        return infoOfSite.getDate();
    }

    @Override
    public String getTitleSite() {
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
            TAG_CLASS = ".table";
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
}
