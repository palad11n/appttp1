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

    public abstract String getLastChapter(Elements rows);

    public void setTitle(String title, String link) {
        if (title == null || title.isEmpty())
            this.title = link;
        else this.title = title.trim();
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
            if (date.contains(" ") || date.contains("oday"))
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
        if (date.contains("oday")){
            Date dateNow = new Date();
            SimpleDateFormat formatForDateNow = new SimpleDateFormat("dd.MM.yy");
            return formatForDateNow.format(dateNow);
        }

        String[] datesArr = date
                .replaceAll(",", " ")
                .replaceAll("\\s{2,}", " ")
                .split(" ");
        String[] dates = new String[3];
        int index = 0;
        for (int i = 0; i < 3; i++) {
            String item = datesArr[i];
            if (!isOnlyDigits(item)) {
                index = i;
                dates[1] = item.toLowerCase();
                break;
            }
        }

        if (index == 0)
            dates[0] = datesArr[1];
        else dates[0] = datesArr[0];

        dates[2] = datesArr[2];
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

        dates[2] = dates[2].contains(":") ? getCurrentYear() : dates[2];
        dates[2] = dates[2].length() == 4 ? dates[2].substring(2, 4) : dates[2];

        date = dates[0] + "." + dates[1] + "." + dates[2];
        return date;
    }

    private static boolean isOnlyDigits(String str) {
        return str.matches("[\\d]+");
    }

    private static String getCurrentYear() {
        java.util.Calendar calendar = java.util.Calendar.getInstance(java.util.TimeZone.getDefault(), java.util.Locale.getDefault());
        calendar.setTime(new java.util.Date());
        return String.valueOf(calendar.get(java.util.Calendar.YEAR));
    }
}
