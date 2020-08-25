package com.whenupdate.tools.service.parsers;

import com.whenupdate.tools.service.sites.InfoOfSite;

import org.jsoup.select.Elements;

public class ArchiveOfOurOwnParsing extends InfoOfSite {
    @Override
    public String getLastDate(Elements rows) {
        return rows.select("dd.status").text(); //не загружается
    }

    @Override
    public String getLastChapter(Elements rows) {
        return rows.select("dd.chapters").text();
    }
}
