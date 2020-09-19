package com.whenupdate.tools.service.parsers;

import com.whenupdate.tools.service.sites.InfoOfSite;

import org.jsoup.select.Elements;

public class FanfictionParsing extends InfoOfSite {
    @Override
    public String getLastDate(Elements rows) {
        String date = "";
        try {
            date = rows.select("span.xgray.xcontrast_txt > span").first().attr("data-xutime");
        } catch (Exception e) {
            if (date.isEmpty()) {
                date = rows.select("#content > span:nth-child(7)").attr("data-xutime");
            }
        }

        return date;
    }

    @Override
    public String getLastChapter(Elements rows) {
        String chapter = "";
        String info = "";
        try {
            info = rows.text();
            chapter = info.substring(info.indexOf("Chapt"), info.indexOf("- Words"));
        } catch (Exception e) {
            if (chapter.isEmpty()) {
                info = rows.select("div:nth-child(7)").text();
                int temp = info.indexOf("Next");
                int len = temp != -1 ? temp : info.indexOf("Review");
                chapter = info.substring(0, len);
            }
        }

        return chapter;
    }
}
