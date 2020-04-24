package ru.csu.ttpapp.service.strategy;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import ru.csu.ttpapp.service.InfoOfSite;
import ru.csu.ttpapp.service.ParsingTread;

public class SiteUpdate implements IStrategy {
    private final String linkUsers;
    private InfoOfSite infoOfSite;
    private String TAG_CLASS;
    private static final Map<String, String> map;

    static {
        map = new HashMap<>();
        map.put("https://soundcloud.com/", "time");
        map.put("http://hdseria.tv/", ".epscape_tr");//todo
    }

    public SiteUpdate(String site) {
        linkUsers = site;
        for (String i : map.keySet()) {
            if (site.contains(i)) {
                TAG_CLASS = map.get(i);
                break;
            }
        }
        parsing(linkUsers, TAG_CLASS);

    }

    public SiteUpdate(String site, String tag) {
        linkUsers = site;
        TAG_CLASS = tag;
        parsing(linkUsers, TAG_CLASS);
    }

    @Override
    public Date findUpDate() {
        parsing(linkUsers, TAG_CLASS);
        return infoOfSite.getDate();
    }

    @Override
    public String getTitleSite() {
        return infoOfSite.getTitle();
    }

    private void parsing(String link, String tag) {
        ParsingTread pt = new ParsingTread();
        pt.execute(link, tag);
        try {
            ArrayList<String> resultParse = pt.get();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");//
            Date date = formatter.parse(resultParse.get(1).replaceAll("Z$", "+0000"));
            infoOfSite = new InfoOfSite(resultParse.get(0), date);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
