package com.whenupdate.tools.service.parsers;

import com.whenupdate.tools.service.sites.InfoOfSite;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class MangaReaderParsing extends InfoOfSite {
    @Override
    public String getLastDate(Elements rows) {
        return findElement(rows, 1);
    }

    @Override
    public String getLastChapter(Elements rows) {
        return findElement(rows, 0);
    }

    private String findElement(Elements rows, int column) {
        try {
            Element row = rows.last();
            Elements cols = row.select("td");
            String elem = cols.get(column).text();
            return elem.replace(":", "");
        } catch (Exception e) {
            return "";
        }
    }
}
