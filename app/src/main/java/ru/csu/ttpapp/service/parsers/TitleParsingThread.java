package ru.csu.ttpapp.service.parsers;

import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class TitleParsingThread extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String... strings) {
        Document doc;
        String title = "";
        try {
            doc = Jsoup.connect(strings[0])
                    .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:23.0) Gecko/20100101 Firefox/23.0")
                    .maxBodySize(0)
                    .timeout(5000)
                    .get();
            title = doc.title();
        } catch (Exception ex) { }

        return title;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }
}
