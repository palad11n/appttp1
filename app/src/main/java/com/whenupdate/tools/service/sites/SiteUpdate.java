package com.whenupdate.tools.service.sites;

import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.Date;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import com.whenupdate.tools.service.parsers.FindAnimeParsing;
import com.whenupdate.tools.service.parsers.MangalibParsing;
import com.whenupdate.tools.service.parsers.SerialMovieParsing;
import com.whenupdate.tools.service.parsers.SoundCloudParsing;

public class SiteUpdate implements ISite {
    private final String linkUsers;
    private final Date lastDate;
    private InfoOfSite infoOfSite;
    private String TAG_CLASS;
    private static final Map<String, String> map = Const.MAP_TAG_FOR_LINK;

    public interface ICompleteCallback {
        void onComplete(int result, Date newDate);
    }

    public interface ICompleteCallbackTitle {
        void onComplete(String title);
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
        } else if (linkUsers.contains("//mangalib.me")) {
            infoOfSite = new MangalibParsing();
        } else {
            infoOfSite = new FindAnimeParsing();
            TAG_CLASS = ".table td.hidden-xxs";
        }
    }

    /**
     * Ищет обновление - последнюю дату - для сравнения с текущей датой в задаче.
     *
     * @param iCompleteCallback отклик после выполнений методов для уведомлений пользователя
     */
    public void findUpDate(ICompleteCallback iCompleteCallback) {
        getDateFromSite()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(date -> {
                            //Log.i("@@@", date);
                            infoOfSite.setDate(date);
                            Date reqDate = infoOfSite.getDate();
                            iCompleteCallback.onComplete(isUpdate(reqDate), reqDate);
                        },
                        error -> Log.e("@@@", error.getMessage())
                );
    }

    private Observable<String> getDateFromSite(){
        return  Observable.fromCallable(() -> {
            Document doc;
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
        });
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
                return 1; // есть обновление
            }
        } else return -1; // ошибка

        return 0;         // нет обновлений
    }

    /**
     * Получить название страницы для создания задачи.
     *
     * @param iCompleteCallback отклик после выполнений методов для уведомлений пользователя
     */
    @Override
    public void getTitleSite(ICompleteCallbackTitle iCompleteCallback) {
        Observable.fromCallable(() -> {
            Document doc;
            try {
                doc = Jsoup.connect(linkUsers)
                        .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:23.0) Gecko/20100101 Firefox/23.0")
                        .maxBodySize(0)
                        .timeout(5000)
                        .get();
                return doc.title();
            } catch (Exception ex) {
            }
            return "";
        })
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.io())
                .subscribe(title -> {
                            //Log.i("@@@", title);
                            infoOfSite.setTitle(title, linkUsers);
                            iCompleteCallback.onComplete(infoOfSite.getTitle());
                        },
                        error -> Log.e("@@@", error.getMessage())
                );
    }

    /**
     * Ищет последнюю дату обновления для создания задачи.
     *
     * @param iCompleteCallback отклик после выполнений методов для уведомлений пользователя
     */
    @Override
    public void findDate(ICompleteCallback iCompleteCallback) {
        getDateFromSite()
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.io())
                .subscribe(date -> {
                            //Log.i("@@@", date);
                            infoOfSite.setDate(date);
                            Date reqDate = infoOfSite.getDate();
                            iCompleteCallback.onComplete(isUpdate(reqDate), reqDate);
                        },
                        error -> Log.e("@@@", error.getMessage())
                );
    }
}


