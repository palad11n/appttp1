package ru.csu.ttpapp.service;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;

public class ParsingTread extends AsyncTask<String, Void, ArrayList<String>> {

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected ArrayList<String> doInBackground(String... strings) {
        Document doc;
        Element elementDate;
        ArrayList<String> elemResult = new ArrayList<String>();
        try {
            doc = Jsoup.connect(strings[0])
                    .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:23.0) Gecko/20100101 Firefox/23.0")
                    .maxBodySize(0)
                    .get();
            elemResult.add(doc.title());
            if (strings.length > 1) {
                elementDate = doc.select(strings[1]).first();
                elemResult.add(elementDate.text());
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return elemResult;
    }

    @Override
    protected void onPostExecute(ArrayList<String> result) {
        super.onPostExecute(result);
    }
}
