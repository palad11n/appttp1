package ru.csu.ttpapp.service.parsers;

import org.jsoup.select.Elements;

import ru.csu.ttpapp.service.sites.InfoOfSite;

public class MangalibParsing extends InfoOfSite {
    @Override
    public String getLastDate(Elements rows) {
        return rows.get(0).text();
    }
}
