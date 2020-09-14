package com.whenupdate.tools.service.parsers;

import com.whenupdate.tools.service.sites.InfoOfSite;

import org.jsoup.select.Elements;

public class SovetromanticaParsing extends InfoOfSite {
    @Override
    public String getLastDate(Elements rows) {
        return "";
    }

    @Override
    public String getLastChapter(Elements rows) {
        String episode = rows.select("a > div > span").last().text();
        return episode;
    }
}
