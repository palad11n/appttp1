package com.whenupdate.tools.service.parsers;

import com.whenupdate.tools.service.sites.InfoOfSite;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class MangaReaderParsing extends InfoOfSite {
    @Override
    public String getLastDate(Elements rows) {
        return findElement(rows);
    }

    @Override
    public String getLastChapter(Elements rows) {
        return findElement(rows);
    }

    private String findElement(Elements rows) {
        try {
            Element row = rows.first();
            Elements cols = row.select(".xanh");
            String elem = cols.text();
            return elem;
        } catch (Exception e) {
            return "";
        }
    }
}
