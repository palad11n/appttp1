package com.whenupdate.tools.service.parsers;

import com.whenupdate.tools.service.sites.InfoOfSite;

import org.jsoup.select.Elements;

public class MangaFoxParsing extends InfoOfSite {
    @Override
    public String getLastDate(Elements rows) {
        return findElement(rows.select("p.title2"));
    }

    @Override
    public String getLastChapter(Elements rows) {
        return findElement(rows.select("p.title3"));
    }

    private String findElement(Elements rows) {
        for (int i = 0; i < rows.size(); i++) {
            String td = rows.get(i).text();
            if (td != null) {
                return td;
            }
        }
        return "";
    }
}
