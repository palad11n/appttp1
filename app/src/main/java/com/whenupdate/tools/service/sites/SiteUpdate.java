package com.whenupdate.tools.service.sites;

import android.annotation.SuppressLint;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.Date;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import com.whenupdate.tools.service.parsers.ArchiveOfOurOwnParsing;
import com.whenupdate.tools.service.parsers.FanfictionParsing;
import com.whenupdate.tools.service.parsers.FicbookParsing;
import com.whenupdate.tools.service.parsers.FindAnimeParsing;
import com.whenupdate.tools.service.parsers.MangaReaderParsing;
import com.whenupdate.tools.service.parsers.MangaFoxParsing;
import com.whenupdate.tools.service.parsers.MangaHubParsing;
import com.whenupdate.tools.service.parsers.MangalibParsing;
import com.whenupdate.tools.service.parsers.SerialMovieParsing;
import com.whenupdate.tools.service.parsers.SoundCloudParsing;
import com.whenupdate.tools.service.parsers.SovetromanticaParsing;

public class SiteUpdate implements ISite {
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:23.0) Gecko/20100101 Firefox/23.0";
    private static final String REFERRER = "https://www.google.com";
    private final String linkUsers;
    private final Date lastDate;
    private final String lastChapter;
    private InfoOfSite infoOfSite;
    private String TAG_CLASS;
    private static final Map<String, String> map = Const.MAP_TAG_FOR_LINK;

    public interface ICompleteCallback {
        void onComplete(int result, Date newDate, String chapter);
    }

    public interface ICompleteCallbackTitle {
        void onComplete(String title);
    }

    public SiteUpdate(String site, Date last, String chapter) {
        linkUsers = site;
        lastDate = last;
        if (chapter != null)
            lastChapter = chapter;
        else lastChapter = "";

        for (String i : map.keySet()) {
            if (site.contains(i)) {
                TAG_CLASS = map.get(i);
                break;
            }
        }

        if (site.contains("://m.fanfox.net/"))
            TAG_CLASS = ".chlist a";
        if (site.contains("://m.fanfiction.net/"))
            TAG_CLASS = "#top";

        if (linkUsers.contains("soundcloud.com/")) {
            infoOfSite = new SoundCloudParsing();
        } else if (linkUsers.contains("seria")) {
            infoOfSite = new SerialMovieParsing();
        } else if (linkUsers.contains("mangalib.me/")) {
            infoOfSite = new MangalibParsing();
        } else if (linkUsers.contains("ficbook.net/")) {
            infoOfSite = new FicbookParsing();
        } else if (linkUsers.contains("mangahub")) {
            infoOfSite = new MangaHubParsing();
        } else if (linkUsers.contains("mangafox") || linkUsers.contains("fanfox.net/")) {
            infoOfSite = new MangaFoxParsing();
        } else if (linkUsers.contains("mangareader")) {
            infoOfSite = new MangaReaderParsing();
        } else if (linkUsers.contains("sovetromantica")) {
            infoOfSite = new SovetromanticaParsing();
        } else if (linkUsers.contains("fanfiction.")) {
            infoOfSite = new FanfictionParsing();
        } else if (linkUsers.contains("archiveofourown.org/")) {
            infoOfSite = new ArchiveOfOurOwnParsing();
        } else {
            infoOfSite = new FindAnimeParsing();
        }
    }

    /**
     * Ищет обновление - последнюю дату - для сравнения с текущей датой в задаче.
     *
     * @param iCompleteCallback отклик после выполнений методов для уведомлений пользователя
     */
    @SuppressLint("CheckResult")
    public void findUpDate(ICompleteCallback iCompleteCallback) {
        if (TAG_CLASS == null) {
            iCompleteCallback.onComplete(-2, null, null);
            return;
        }
        getDateFromSite()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(info -> {
                            //Log.i("@@@", info[1]);
                            infoOfSite.setDate(info[0]);
                            infoOfSite.setTitle(info[1], "");
                            Date reqDate = infoOfSite.getDate();
                            String chapter = infoOfSite.getTitle();
                            iCompleteCallback.onComplete(isUpdate(reqDate, chapter), reqDate, chapter);
                        },
                        error -> Log.e("@@@", error.getMessage())
                );
    }

    private Observable<String[]> getDateFromSite() {
        return Observable.fromCallable(() -> {
            Document doc;
            String[] row = new String[2];
            row[0] = "";
            row[1] = "";
            try {
                doc = Jsoup.connect(linkUsers)
                        .userAgent(USER_AGENT)
                        .referrer(REFERRER)
                        .maxBodySize(0)
                        .timeout(10000)
                        .get();
                Elements rows = doc.select(TAG_CLASS);
                row[0] = infoOfSite.getLastDate(rows);
                row[1] = infoOfSite.getLastChapter(rows);
                return row;
            } catch (Exception e) {
            }
            return row;
        });
    }

    /**
     * Метод сравнивает сохраненную дату и пришедшую, чтобы выявить наличие обновления.
     *
     * @param newDate дата, которая только, что пришла с сайта
     * @return 1 - значит обновление есть; -1 - дата не пришла; 0 - нет обновлений
     */
    private int isUpdate(Date newDate, String chapter) {
        if (newDate != null || chapter != null) {
            if (newDate != null && newDate.after(lastDate)) {
                return 1; // есть обновление
            } else {
                if (lastChapter != null
                        && ((lastChapter.isEmpty() && !chapter.isEmpty())
                        || !chapter.contains(lastChapter)))
                    return 1;
            }
        } else return -1; // ошибка

        return 0;         // нет обновлений
    }

    /**
     * Получить название страницы для создания задачи.
     *
     * @param iCompleteCallback отклик после выполнений методов для уведомлений пользователя
     */
    @SuppressLint("CheckResult")
    @Override
    public void getTitleSite(ICompleteCallbackTitle iCompleteCallback) {
        //WebDriver driver =new FirefoxDriver();
        //Builder request = new Request.Builder().url(url);
        Observable.fromCallable(() -> {
            final Document doc;
            try {
                doc = Jsoup.connect(linkUsers)
                        .userAgent(USER_AGENT)
                        .referrer(REFERRER)
                        .timeout(10000)
                        .ignoreHttpErrors(true)
                        .get();
                String title = doc.title();
                return title;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return "";
        })
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.io())
                .subscribe(title -> {
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
    @SuppressLint("CheckResult")
    @Override
    public void findDate(ICompleteCallback iCompleteCallback) {
        getDateFromSite()
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.io())
                .subscribe(info -> {
                            //Log.i("@@@", info[1]);
                            infoOfSite.setDate(info[0]);
                            Date reqDate = infoOfSite.getDate();
                            infoOfSite.setTitle(info[1], "");
                            String chapter = infoOfSite.getTitle();
                            iCompleteCallback.onComplete(isUpdate(reqDate, chapter), reqDate, chapter);
                        },
                        error -> Log.e("@@@", error.getMessage())
                );
    }
}


