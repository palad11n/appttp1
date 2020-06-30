package com.whenupdate.tools.service.parsers;

import org.jsoup.select.Elements;

import com.whenupdate.tools.service.sites.InfoOfSite;

public class MangalibParsing extends InfoOfSite {
    @Override
    public String getLastDate(Elements rows) {
        return rows.get(0).text();
    }
}
