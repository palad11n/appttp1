package com.whenupdate.tools.service.sites;

import java.util.HashMap;
import java.util.Map;

/**
 * Константные значения для парсеров
 */
class Constants {
    public static Map<String, String> getTags() {
        Map<String, String> mapTagForLink = new HashMap<>();
        mapTagForLink.put("seria", ".epscape_tr");
        mapTagForLink.put("findanime", ".table tr");
        mapTagForLink.put("mintmanga", ".table tr");
        mapTagForLink.put("readmanga", ".table tr");
//        mapTagForLink.put("mangalib.me", ".chapter-item");
        mapTagForLink.put("ficbook.net", ".part ");
        mapTagForLink.put("mangahub", ".d-flex.flex-column");
        mapTagForLink.put("sovetromantica.com/", ".episodes-slick_item");

        mapTagForLink.put("mangareader", "table tr");
        mapTagForLink.put("fanfox.net", ".detail-main-list-main");
        mapTagForLink.put("mangafox", ".detail-main-list-main");
        mapTagForLink.put("soundcloud.com/", "article.audible");
      //  mapTagForLink.put("fanfiction.", "span.xgray.xcontrast_txt");
        mapTagForLink.put("archiveofourown.org/", "dl.stats");
        return mapTagForLink;
    }

    public static Map<String, Integer> getMonth() {
        Map<String, Integer> months = new HashMap<>();
        months.put("янв", 1);
        months.put("фев", 2);
        months.put("мар", 3);
        months.put("апр", 4);
        months.put("мая", 5);
        months.put("июн", 6);
        months.put("июл", 7);
        months.put("авг", 8);
        months.put("сен", 9);
        months.put("окт", 10);
        months.put("ноя", 11);
        months.put("дек", 12);

        months.put("jan", 1);
        months.put("feb", 2);
        months.put("mar", 3);
        months.put("apr", 4);
        months.put("may", 5);
        months.put("jun", 6);
        months.put("jul", 7);
        months.put("aug", 8);
        months.put("sep", 9);
        months.put("oct", 10);
        months.put("nov", 11);
        months.put("dec", 12);
        return months;
    }
}
