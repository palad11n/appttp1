package com.whenupdate.tools.service.sites;

import java.util.HashMap;
import java.util.Map;

/**
 * Константные значения для парсеров
 */
class Const {
    static final Map<String, String> MAP_TAG_FOR_LINK;
    static final Map<String, Integer> MONTHS;

    static {
        MAP_TAG_FOR_LINK = new HashMap<>();
        MAP_TAG_FOR_LINK.put("seria", ".epscape_tr");
        MAP_TAG_FOR_LINK.put("findanime.me/", ".table tr");
        MAP_TAG_FOR_LINK.put("mintmanga", ".table tr");
        MAP_TAG_FOR_LINK.put("readmanga", ".table tr");
        MAP_TAG_FOR_LINK.put("mangalib.me", ".chapter-item");
        MAP_TAG_FOR_LINK.put("ficbook.net", ".part-info");
        MAP_TAG_FOR_LINK.put("mangahub", ".d-flex.flex-column");
        MAP_TAG_FOR_LINK.put("sovetromantica.com/", ".episodes-slick_item");

        MAP_TAG_FOR_LINK.put("mangareader.net", "table#listing tr");
        MAP_TAG_FOR_LINK.put("//fanfox.net", ".detail-main-list-main");
        MAP_TAG_FOR_LINK.put("//mangafox", ".detail-main-list-main");
        MAP_TAG_FOR_LINK.put("soundcloud.com/", "article.audible");
    }

    static {
        MONTHS = new HashMap<>();
        MONTHS.put("янв", 1);
        MONTHS.put("фев", 2);
        MONTHS.put("мар", 3);
        MONTHS.put("апр", 4);
        MONTHS.put("мая", 5);
        MONTHS.put("июн", 6);
        MONTHS.put("июл", 7);
        MONTHS.put("авг", 8);
        MONTHS.put("сен", 9);
        MONTHS.put("окт", 10);
        MONTHS.put("ноя", 11);
        MONTHS.put("дек", 12);

        MONTHS.put("jan", 1);
        MONTHS.put("feb", 2);
        MONTHS.put("mar", 3);
        MONTHS.put("apr", 4);
        MONTHS.put("may", 5);
        MONTHS.put("jun", 6);
        MONTHS.put("jul", 7);
        MONTHS.put("aug", 8);
        MONTHS.put("sept", 9);
        MONTHS.put("oct", 10);
        MONTHS.put("nov", 11);
        MONTHS.put("dec", 12);
    }
}
