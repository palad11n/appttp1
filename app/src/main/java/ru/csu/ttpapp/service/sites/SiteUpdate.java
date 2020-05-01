package ru.csu.ttpapp.service.sites;

import android.os.AsyncTask;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import ru.csu.ttpapp.service.parsers.ParsingSoundCloudThread;
import ru.csu.ttpapp.service.parsers.SerialMovieParsingThread;
import ru.csu.ttpapp.service.parsers.TitleParsingThread;

public class SiteUpdate implements ISite {
    private final String linkUsers;
    private InfoOfSite infoOfSite;
    private String TAG_CLASS;
    private static final Map<String, String> map;

    static {
        map = new HashMap<>();
        map.put("https://soundcloud.com/", "time");
        map.put("http://hdseria.tv/", ".epscape_tr");
        map.put("https://7serialov.net/", ".epscape_tr");
    }

    public SiteUpdate(String site) {
        linkUsers = site;
        parsingTitle();
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
        return infoOfSite.getTitle();
    }

    private void parsingDate() {
        AsyncTask<String, Void, String> pt;
        if (linkUsers.contains("https://soundcloud.com/")) {
            pt = new ParsingSoundCloudThread();
        } else {
            pt = new SerialMovieParsingThread();
        }
        pt.execute(linkUsers, TAG_CLASS);
        try {
            infoOfSite.setDate(pt.get());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parsingTitle() {
        TitleParsingThread pt = new TitleParsingThread();
        pt.execute(linkUsers);
        try {
            infoOfSite = new InfoOfSite(pt.get());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
