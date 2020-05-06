package ru.csu.ttpapp.service.parsers;

import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class TitleParsingThread extends AsyncTask<String, Void, String> {
    @Override
    protected String doInBackground(String... strings) {
        Document doc;
        String title = "";
        try {
            doc = Jsoup.connect(strings[0])
                    .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:23.0) Gecko/20100101 Firefox/23.0")
                    .maxBodySize(0)
                    .get();
            title = doc.title();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return title;
    }
}
