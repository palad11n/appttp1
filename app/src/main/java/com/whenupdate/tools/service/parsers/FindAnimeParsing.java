package com.whenupdate.tools.service.parsers;

import org.jsoup.select.Elements;

import com.whenupdate.tools.service.sites.InfoOfSite;


public class FindAnimeParsing extends InfoOfSite {
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
