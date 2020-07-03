package com.whenupdate.tools.service.parsers;

import com.whenupdate.tools.service.sites.InfoOfSite;

import org.jsoup.select.Elements;


public class FindAnimeParsing extends InfoOfSite {
    @Override
    public String getLastDate(Elements rows) {
        return findElement(rows, "td.hidden-xxs");
    }

    @Override
    public String getLastChapter(Elements rows) {
        return findElement(rows, "td a");
    }

    private String findElement(Elements rows, String tag) {
        rows = rows.select(tag);
        for (int i = 0; i < rows.size(); i++) {
            String td = rows.get(i).text();
            if (td != null) {
                return td;
            }
        }
        return "";
    }


}
