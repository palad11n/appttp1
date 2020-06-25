package ru.csu.ttpapp.service.sites;

public interface ISite {
    void findUpDate(SiteUpdate.ICompleteCallback iCompleteCallback);
    String getTitleSite();
}
