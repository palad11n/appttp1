package ru.csu.ttpapp.service.parsers;

import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import ru.csu.ttpapp.service.sites.InfoOfSite;
import ru.csu.ttpapp.service.sites.SiteUpdate;

public class ParsingSoundCloudThread extends InfoOfSite {
    @Override
    public String getLastDate(Elements rows) {
        Element elementDate;
        elementDate = rows.first();

        return elementDate.text();
    }
}
