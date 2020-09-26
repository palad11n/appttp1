package com.whenupdate.tools.service.parsers;

import com.whenupdate.tools.service.sites.InfoOfSite;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class SoundCloudParsing extends InfoOfSite {
    @Override
    public String getLastDate(Elements rows) {
        try {
            Element elementDate = rows.select("time").first();
            return elementDate.text();

        } catch (Exception e) {
        }
        return "";

    }

    @Override
    public String getLastChapter(Elements rows) {
        try {
            Element elementPost = rows
                    .select("h2[itemprop=name]")
                    .select("a[itemprop=url]").first();
            return elementPost.text();
        } catch (Exception e) {
        }
        return "";
    }
}
