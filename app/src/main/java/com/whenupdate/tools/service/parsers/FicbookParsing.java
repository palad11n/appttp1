package com.whenupdate.tools.service.parsers;

import com.whenupdate.tools.service.sites.InfoOfSite;

import org.jsoup.select.Elements;

public class FicbookParsing extends InfoOfSite {
    @Override
    public String getLastDate(Elements rows) {
        return findElement(rows.select(".text-muted span"));
    }

    @Override
    public String getLastChapter(Elements rows) {
        return findElement(rows.select(".part-title"));
    }

    private String findElement(Elements rows) {
        return rows.last().text();
    }
}
