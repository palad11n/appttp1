package com.whenupdate.tools.service.sites;

import org.jsoup.select.Elements;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public abstract class InfoOfSite {
    private Date date;
    private String title;
    private static final Map<String, Integer> months = Const.MONTHS;

    public InfoOfSite(String title, String date) {
        this.title = title;
        try {
            this.date = convertToDate(date);
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
    }

    public InfoOfSite() {
    }

    public abstract String getLastDate(Elements rows);

    public void setTitle(String title, String link) {
        if (title == null || title.isEmpty() || title.equals(""))
            this.title = link;
        else this.title = title;
    }

    public void setDate(String date) {
        try {
            this.date = convertToDate(date);
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
    }

    public Date getDate() {
        return date;
    }

    public String getTitle() {
        return title;
    }

    private Date convertToDate(String date) throws ParseException {
        if (date.equals("")) return null;
        Date fromSite;
        SimpleDateFormat formatter;
        if (!date.contains("Z")) {
            if (date.contains(" "))
                date = convertToMonth(date);

            fromSite = new SimpleDateFormat("dd.MM.yy").parse(date);
        } else {
            formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
            date = date.replaceAll("Z$", "+0000");
            fromSite = formatter.parse(date);
        }
        return fromSite;
    }

    private String convertToMonth(String date) {
        String[] dates = date.replaceAll(",", "").split(" ");
        for (String month : months.keySet()) {
            if (dates[1].contains(month)) {
                Integer numMonth = months.get(month);
                dates[1] = numMonth < 10 ? "0" + numMonth.toString() : numMonth.toString();
                break;
            }
        }
        if (!isOnlyDigits(dates[1]))
            throw new IllegalArgumentException("Invalid month format: " + dates[1]);

        dates[0] = Integer.parseInt(dates[0]) < 10 ? "0" + dates[0] : dates[0];
        dates[2] = dates[2].length() == 4 ? dates[2].substring(2, 4) : dates[2];

        date = dates[0] + "." + dates[1] + "." + dates[2];
        return date;
    }

    private static boolean isOnlyDigits(String str) {
        return str.matches("[\\d]+");
    }
}
