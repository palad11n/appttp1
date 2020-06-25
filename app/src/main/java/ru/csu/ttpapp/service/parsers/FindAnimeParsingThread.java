package ru.csu.ttpapp.service.parsers;

import org.jsoup.select.Elements;

import ru.csu.ttpapp.service.sites.InfoOfSite;


public class FindAnimeParsingThread extends InfoOfSite {
    @Override
    public String getLastDate(Elements rows) {
        for (int i = 0; i < rows.size(); i++) {
            String td = rows.get(i).text();
            if (td != null) {
                return td;
            }
        }
        return "";
    }
}
