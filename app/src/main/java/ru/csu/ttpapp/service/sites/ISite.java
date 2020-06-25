package ru.csu.ttpapp.service.sites;

import java.util.Date;

public interface ISite {
    void findUpDate(SiteUpdate.IComplete iComplete);
    String getTitleSite();
}
