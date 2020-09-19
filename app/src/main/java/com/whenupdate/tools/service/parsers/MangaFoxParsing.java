package com.whenupdate.tools.service.parsers;

import com.whenupdate.tools.service.sites.InfoOfSite;

import org.jsoup.select.Elements;

public class MangaFoxParsing extends InfoOfSite {
    @Override
    public String getLastDate(Elements rows) {
        String result = findElement(rows.select("p.title2"));
        if (result.isEmpty())
            result = findElement(rows.select(":not(a)"));
        return result;
    }

    @Override
    public String getLastChapter(Elements rows) {
        String result = findElement(rows.select("p.title3"));
        if (result.isEmpty()) {
            String tempChDate = findElement(rows).trim();
            int firstSpace = tempChDate.indexOf(' ') + 1;
            int twoSpace = tempChDate.indexOf(' ', firstSpace);
            result = tempChDate.substring(0, twoSpace);
        }

        return result;
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
