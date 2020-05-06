package ru.csu.ttpapp.service.sites;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InfoOfSite {
    private Date date;
    private String title;

    private static final Map<String, Integer> months;

    static {
        months = new HashMap<>();
        months.put("янв", 1);
        months.put("фев", 2);
        months.put("мар", 3);
        months.put("апр", 4);
        months.put("мая", 5);
        months.put("июн", 6);
        months.put("июл", 7);
        months.put("авг", 8);
        months.put("сен", 9);
        months.put("окт", 10);
        months.put("ноя", 11);
        months.put("дек", 12);
    }

    public InfoOfSite(String title, String date) {
        this.title = title;
        try {
            this.date = convertToDate(date);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public InfoOfSite(String title) {
        this.title = title;
    }

    public InfoOfSite() {

    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDate(String date) {
        try {
            this.date = convertToDate(date);
        } catch (Exception ex) {
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
            String newDate;
            if (date.contains(" ")) {
                String[] dates = convertToMonth(date);
                newDate = dates[0] + "." + dates[1] + "." + dates[2];
            } else newDate = date;
            fromSite = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                    .parse(newDate);
        } else {
            formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
            date = date.replaceAll("Z$", "+0000");
            fromSite = formatter.parse(date);
        }
        return fromSite;
    }

    private String[] convertToMonth(String date) {
        String[] dates = date.replaceAll(",", "").split(" ");
        for (String month : months.keySet()) {
            if (dates[1].equals(month)) {
                Integer numMonth = months.get(month);
                dates[1] = numMonth < 10 ? "0" + numMonth.toString() : numMonth.toString();
                break;
            }
        }
        dates[0] = Integer.parseInt(dates[0]) < 10 ? "0" + dates[0] : dates[0];
        return dates;
    }
}
