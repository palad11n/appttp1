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
        MAP_TAG_FOR_LINK.put("//soundcloud.com/", "article.audible");
        MAP_TAG_FOR_LINK.put("seria", ".epscape_tr");
        MAP_TAG_FOR_LINK.put("//findanime.me/", ".table tr");
        MAP_TAG_FOR_LINK.put("//mintmanga", ".table tr");
        MAP_TAG_FOR_LINK.put("//readmanga", ".table tr");
        MAP_TAG_FOR_LINK.put("//mangalib.me", ".chapter-item");
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
    }
}
