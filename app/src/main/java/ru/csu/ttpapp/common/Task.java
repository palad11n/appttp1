package ru.csu.ttpapp.common;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Task {
    private long id;
    private String title;
    private String link;
    private Date date = new Date();
    private boolean isUpdate=false;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isUpdate() {
        return isUpdate;
    }

    public void setUpdate(boolean update) {
        isUpdate = update;
    }

    public Date getDate() {
        return date;
    }

    public void setDate() {
        this.date = new Date();
    }

    public String getSimpleDateFormat(){
        SimpleDateFormat formatForDateNow
                = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        String format = formatForDateNow.format(this.date);
        return  format;
    }
}
