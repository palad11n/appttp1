package com.whenupdate.tools.service.parsers;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.whenupdate.tools.service.sites.InfoOfSite;

public class SerialMovieParsing extends InfoOfSite {

    @Override
    public String getLastDate(Elements rows) {
        return findRelease(rows, 2);
    }

    @Override
    public String getLastChapter(Elements rows) {
        return findRelease(rows, 0);
    }

    private String findRelease(Elements rows, int column) {
        for (int i = 0; i < rows.size(); i++) {
            Element row = rows.get(i);
            Elements cols = row.select("td");
            String release = cols.get(3).html();
            if (release.contains("release")) {
                return cols.get(column).text();
            }
        }
        return "";
    }
}
