package ru.csu.ttpapp.service.parsers;

import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class SerialMovieParsingThread extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String... strings) {
        Document doc;
        String date = "";
        try {
            doc = Jsoup.connect(strings[0])
                    .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:23.0) Gecko/20100101 Firefox/23.0")
                    .maxBodySize(0)
                    .get();
            if (strings.length > 1) {
                Elements rows = doc.select(".epscape_tr");
                for (int i = 0; i < rows.size(); i++) {
                    Element row = rows.get(i); //по номеру индекса получает строку
                    Elements cols = row.select("td");// разбиваем полученную строку по тегу на столбы
                    String d = cols.get(3).html();
                    if (d.contains("release")) {
                        date = cols.get(2).text();
                        break;
                    }
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return date;
    }


}
