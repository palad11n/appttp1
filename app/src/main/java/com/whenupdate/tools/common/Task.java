package com.whenupdate.tools.common;

import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Task {
    @NonNull
    private long id = -1L;
    @NonNull
    private String link;
    private String title;
    private String chapter;
    private String icon;
    @NonNull
    private Date date = new Date();
    private boolean isUpdate = false;

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @NonNull
    public String getLink() {
        return link;
    }

    public void setLink(@NonNull String link) {
        this.link = link;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        if (title == null) title = "";
        this.title = title;
    }

    public boolean isUpdate() {
        return isUpdate;
    }

    public void setUpdate(boolean update) {
        isUpdate = update;
    }

    @NonNull
    public Date getDate() {
        return date;
    }

    public void setDate(@NonNull Date date) {
        this.date = date;
    }

    public String getSimpleDateFormat() {
        SimpleDateFormat formatForDateNow = new SimpleDateFormat("dd.MM.yyyy");
        String format = formatForDateNow.format(date);
        return format;
    }

    public String getChapter() {
        return chapter;
    }

    public void setChapter(String chapter) {
        if (chapter == null)
            chapter = "";
        this.chapter = chapter;
    }

}
