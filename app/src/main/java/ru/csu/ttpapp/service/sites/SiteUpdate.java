package ru.csu.ttpapp.service.sites;

import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import ru.csu.ttpapp.service.parsers.FindAnimeParsing;
import ru.csu.ttpapp.service.parsers.SoundCloudParsing;
import ru.csu.ttpapp.service.parsers.SerialMovieParsing;
import ru.csu.ttpapp.service.parsers.TitleParsingThread;

public class SiteUpdate implements ISite {
    private final String linkUsers;
    private final Date lastDate;
    private InfoOfSite infoOfSite;
    private String TAG_CLASS;
    private static final Map<String, String> map;

    static {
        map = new HashMap<>();
        map.put("//soundcloud.com/", "time");
        map.put("seria", ".epscape_tr");
        map.put("//findanime.me/", ".table td.hidden-xxs");
        map.put("//mintmanga", ".table td.hidden-xxs");
        map.put("//readmanga", ".table td.hidden-xxs");
    }

    public interface ICompleteCallback {
        void onComplete(int result, Date newDate);
    }

    public SiteUpdate(String site, Date last) {
        linkUsers = site;
        lastDate = last;
        for (String i : map.keySet()) {
            if (site.contains(i)) {
                TAG_CLASS = map.get(i);
                break;
            }
        }

        if (linkUsers.contains("//soundcloud.com/")) {
            infoOfSite = new SoundCloudParsing();
        } else if (linkUsers.contains("seria")) {
            infoOfSite = new SerialMovieParsing();
        } else {
            infoOfSite = new FindAnimeParsing();
            TAG_CLASS = ".table td.hidden-xxs";
        }

    }

    /**
     * Запускает парсинг страницы для поиска последней даты.
     *
     * @param iCompleteCallback отклик после выполнений методов для уведомлений пользователя
     */
    public void findUpDate(ICompleteCallback iCompleteCallback) {
        Observable.fromCallable(() -> {
            Document doc;
            //  String date = "";
            try {
                doc = Jsoup.connect(linkUsers)
                        .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:23.0) Gecko/20100101 Firefox/23.0")
                        .maxBodySize(0)
                        .timeout(5000)
                        .get();
                Elements rows = doc.select(TAG_CLASS);
                return infoOfSite.getLastDate(rows);
            } catch (Exception e) {
            }
            return "";
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(date -> {
                            Log.i("@@@", date);
                            infoOfSite.setDate(date);
                            Date reqDate = infoOfSite.getDate();
                            iCompleteCallback.onComplete(isUpdate(reqDate), reqDate);
                        },
                        error -> Log.e("@@@", error.getMessage())
                );
    }

    /**
     * Метод сравнивает сохраненную дату и пришедшую, чтобы выявить наличие обновления.
     *
     * @param newDate дата, которая только, что пришла с сайта
     * @return 1 - значит обновление есть; -1 - дата не пришла; 0 - нет обновлений
     */
    private int isUpdate(Date newDate) {
        if (newDate != null) {
            if (newDate.after(lastDate)) {
                return 1; // обновление
            }
        } else return -1; // ошибка

        return 0;         // нет обновления
    }

    @Override
    public String getTitleSite() {
        parsingTitle();
        return infoOfSite.getTitle();
    }

    private void parsingTitle() {
        TitleParsingThread pt = new TitleParsingThread();
        pt.execute(linkUsers);
        try {
            String title = pt.get();
            infoOfSite.setTitle(title, linkUsers);
        } catch (Exception e) {
        }
    }
}


