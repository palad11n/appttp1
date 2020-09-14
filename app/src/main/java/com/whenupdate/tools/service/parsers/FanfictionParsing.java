package com.whenupdate.tools.service.parsers;

import com.whenupdate.tools.service.sites.InfoOfSite;

import org.jsoup.select.Elements;

public class FanfictionParsing extends InfoOfSite {
    @Override
    public String getLastDate(Elements rows) {
        String date = rows.select("span.xgray.xcontrast_txt > span").first().attr("data-xutime");
        return date;
    }

    @Override
    public String getLastChapter(Elements rows) {
        String info = rows.text();
        String chapter = info.substring(info.indexOf("Chapt"), info.indexOf("- Words"));
        return chapter;
    }
}
