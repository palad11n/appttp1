package com.whenupdate.tools.service.parsers;

import com.whenupdate.tools.service.sites.InfoOfSite;

import org.jsoup.select.Elements;

public class MangaHubParsing extends InfoOfSite {
    @Override
    public String getLastDate(Elements rows) {
        return findElement(rows.select(".ml-2.text-muted.text-nowrap"));
    }

    @Override
    public String getLastChapter(Elements rows) {
        return findElement(rows.select("a.ml-2.text-truncate"));
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
