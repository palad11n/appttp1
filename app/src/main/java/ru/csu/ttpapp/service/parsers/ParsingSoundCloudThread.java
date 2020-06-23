package ru.csu.ttpapp.service.parsers;

import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import ru.csu.ttpapp.service.sites.SiteUpdate;

public class ParsingSoundCloudThread extends AsyncTask<String, Void, String> {

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected String doInBackground(String... strings) {
        Document doc;
        Element elementDate;
        String date = "";
        try {
            doc = Jsoup.connect(strings[0])
                    .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:23.0) Gecko/20100101 Firefox/23.0")
                    .maxBodySize(0)
                    .get();
            if (strings.length > 1) {
                elementDate = doc.select(strings[1]).first();
                date = elementDate.text();
            }
        } catch (Exception ex) { }
        return date;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }
}
