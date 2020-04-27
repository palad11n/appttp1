package ru.csu.ttpapp.service;

import java.util.Date;

public class InfoOfSite {
    private Date date;
    private String title;

    public InfoOfSite(String title, Date date) {
        this.title = title;
        this.date = date;
    }

    public Date getDate() {
        return date;
    }

    public String getTitle() {
        return title;
    }
}
