package ru.csu.ttpapp.service.parsers;

import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class FindAnimeParsingThread extends AsyncTask<String, Void, String> {
    @Override
    protected String doInBackground(String... strings) {
        Document doc;
        String date = "";
        String link = strings[0];
        try {
            doc = Jsoup.connect(link)
                    .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:23.0) Gecko/20100101 Firefox/23.0")
                    .maxBodySize(0)
                    .timeout(5000)
                    .get();
            if (strings.length > 1) {
                Elements rows = doc.select(strings[1]).select(".hidden-xxs");
               date = rows.get(1).text();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return date;
    }
}
