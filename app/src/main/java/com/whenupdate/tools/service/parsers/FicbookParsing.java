package com.whenupdate.tools.service.parsers;

import com.whenupdate.tools.service.sites.InfoOfSite;

import org.jsoup.select.Elements;

public class FicbookParsing extends InfoOfSite {
    @Override
    public String getLastDate(Elements rows) {
        return findElement(rows.select(".small-text.text-muted span"));
    }

    @Override
    public String getLastChapter(Elements rows) {
        return findElement(rows.select(".visit-link"));
    }

    private String findElement(Elements rows) {
        String d = rows.last().text();
        return rows.last().text();
    }
}
