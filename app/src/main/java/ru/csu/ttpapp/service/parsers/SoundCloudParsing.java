package ru.csu.ttpapp.service.parsers;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import ru.csu.ttpapp.service.sites.InfoOfSite;

public class SoundCloudParsing extends InfoOfSite {
    @Override
    public String getLastDate(Elements rows) {
        Element elementDate;
        elementDate = rows.first();

        return elementDate.text();
    }
}
