package ru.csu.ttpapp.service.parsers;

import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import ru.csu.ttpapp.service.sites.InfoOfSite;
import ru.csu.ttpapp.service.sites.SiteUpdate;

public class SerialMovieParsingThread extends InfoOfSite {

    @Override
    public String getLastDate(Elements rows) {
        for (int i = 0; i < rows.size(); i++) {
            Element row = rows.get(i); //по номеру индекса получает строку
            Elements cols = row.select("td");// разбиваем полученную строку по тегу на столбы
            String d = cols.get(3).html();
            if (d.contains("release")) {
                return cols.get(2).text();
            }
        }
        return "";
    }
}
