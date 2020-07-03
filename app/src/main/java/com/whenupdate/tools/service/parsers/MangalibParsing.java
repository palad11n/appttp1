package com.whenupdate.tools.service.parsers;

import org.jsoup.select.Elements;

import com.whenupdate.tools.service.sites.InfoOfSite;

public class MangalibParsing extends InfoOfSite {
    @Override
    public String getLastDate(Elements rows) {
        rows = rows.select(".chapter-item__date");
        return rows.get(0).text();
    }

    @Override
    public String getLastChapter(Elements rows) {
        rows = rows.select(".chapter-item__name a");
        return rows.get(0).text();
    }
}
