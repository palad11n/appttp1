package com.whenupdate.tools.service.parsers;

import com.whenupdate.tools.service.sites.InfoOfSite;

import org.jsoup.select.Elements;


public class FindAnimeParsing extends InfoOfSite {
    @Override
    public String getLastDate(Elements rows) {
        return findElement(rows.select("td.hidden-xxs"));
    }

    @Override
    public String getLastChapter(Elements rows) {
        rows = rows.select("td a:not(.person-link)");
        for (int i = 0; i < rows.size(); i++) {
            String td = rows.get(i).childNodes().get(0).outerHtml();
            if (td != null) {
                return td;
            }
        }
        return "";
    }

    private String findElement(Elements rows) {
        for (int i = 0; i < rows.size(); i++) {
            String td = rows.get(i).text();
            if (td != null) {
                return td;
            }
        }
        return "";
    }


}
